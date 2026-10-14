package com.qjrpg.api.auth;

import com.qjrpg.api.auth.seguranca.JwtService;
import com.qjrpg.api.auth.seguranca.TotpService;
import com.qjrpg.api.moderacao.FiltroDePalavrasService;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    public static final int APELIDO_MIN_CARACTERES = 3;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository repository;
    private final JwtService jwtService;
    private final FiltroDePalavrasService filtro;
    private final TotpService totpService;
    private final List<String> adminEmails;

    public AuthServiceImpl(UsuarioRepository repository, JwtService jwtService, FiltroDePalavrasService filtro,
                            TotpService totpService, @Value("${app.admin-emails:}") String adminEmailsCsv) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.filtro = filtro;
        this.totpService = totpService;
        this.adminEmails = Arrays.stream(adminEmailsCsv.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    @Override
    public String solicitarCodigo(String email) {
        Usuario usuario = repository.findByEmail(email).orElseGet(() -> {
            PapelUsuario papel = adminEmails.contains(email) ? PapelUsuario.ADMIN : PapelUsuario.COMUM;
            return repository.save(new Usuario(email, papel));
        });
        String codigo = gerarCodigo();
        usuario.definirCodigo(codigo, Instant.now().plus(5, ChronoUnit.MINUTES));
        repository.save(usuario);
        return codigo;
    }

    @Override
    public AuthResultado confirmarCodigo(String email, String codigo, String nome, String celular, String apelido,
                                          LocalDate dataNascimento, String sexo, String responsavelEmail) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + email));

        if (!usuario.codigoValido(codigo)) {
            usuario.registrarTentativaFalha();
            repository.save(usuario);
            throw new RegraDeNegocioException("Codigo invalido ou expirado");
        }
        if (usuario.estaSuspenso()) {
            throw new RegraDeNegocioException("Conta suspensa. Reative em Meu perfil ou aguarde o prazo.");
        }

        boolean precisaDeApelido = usuario.getApelido() == null;
        if (precisaDeApelido && (apelido == null || apelido.isBlank())) {
            throw new RegraDeNegocioException("Apelido e obrigatorio no primeiro acesso");
        }
        if (apelido != null && !apelido.isBlank()) {
            validarApelido(usuario, apelido);
        }
        if (dataNascimento != null && dataNascimento.plusYears(18).isAfter(LocalDate.now())
                && (responsavelEmail == null || responsavelEmail.isBlank()) && usuario.getResponsavelEmail() == null) {
            throw new RegraDeNegocioException("Menores de 18 anos precisam informar o e-mail de um responsavel");
        }

        usuario.limparCodigo();
        usuario.completarCadastro(nome, celular, apelido, dataNascimento, sexo, responsavelEmail);
        repository.save(usuario);

        if (usuario.isTotpAtivo()) {
            String desafio = gerarDesafio();
            usuario.definirDesafio2fa(desafio, Instant.now().plus(5, ChronoUnit.MINUTES));
            repository.save(usuario);
            return AuthResultado.segundoFatorPendente(usuario, desafio);
        }
        String token = jwtService.gerarToken(usuario.getId(), usuario.getPapel());
        return AuthResultado.concluido(usuario, token);
    }

    @Override
    public AuthResultado validarSegundoFator(String email, String desafio2fa, String codigoTotp) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + email));
        if (!usuario.isTotpAtivo() || usuario.getTotpSecret() == null) {
            throw new RegraDeNegocioException("2FA nao esta ativo para este usuario");
        }
        // O desafio prova que o primeiro fator (codigo de e-mail) foi concluido ha pouco.
        if (!usuario.desafio2faValido(desafio2fa)) {
            throw new RegraDeNegocioException("Sessao de login expirada. Solicite um novo codigo por e-mail.");
        }
        if (usuario.estaSuspenso()) {
            throw new RegraDeNegocioException("Conta suspensa. Reative em Meu perfil ou aguarde o prazo.");
        }
        if (!totpService.validarCodigo(usuario.getTotpSecret(), codigoTotp)) {
            usuario.registrarTentativaFalha();
            repository.save(usuario);
            throw new RegraDeNegocioException("Codigo do autenticador invalido");
        }
        usuario.limparDesafio2fa();
        repository.save(usuario);
        String token = jwtService.gerarToken(usuario.getId(), usuario.getPapel());
        return AuthResultado.concluido(usuario, token);
    }

    private void validarApelido(Usuario usuario, String apelido) {
        if (apelido.length() < APELIDO_MIN_CARACTERES) {
            throw new RegraDeNegocioException("Apelido deve ter pelo menos " + APELIDO_MIN_CARACTERES + " caracteres");
        }
        if (filtro.contemPalavraProibida(apelido)) {
            throw new RegraDeNegocioException("Apelido contem termo nao permitido");
        }
        repository.findByApelido(apelido).ifPresent(outro -> {
            if (!outro.getId().equals(usuario.getId())) {
                throw new RegraDeNegocioException("Apelido ja esta em uso");
            }
        });
    }

    private String gerarCodigo() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }

    private String gerarDesafio() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
