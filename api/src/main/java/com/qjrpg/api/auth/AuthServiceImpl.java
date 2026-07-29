package com.qjrpg.api.auth;

import com.qjrpg.api.auth.seguranca.JwtService;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repository;
    private final JwtService jwtService;
    private final List<String> adminEmails;

    public AuthServiceImpl(UsuarioRepository repository, JwtService jwtService,
                            @Value("${app.admin-emails:}") String adminEmailsCsv) {
        this.repository = repository;
        this.jwtService = jwtService;
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
    public AuthResultado confirmarCodigo(String email, String codigo, String nome, String celular) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + email));

        if (!usuario.codigoValido(codigo)) {
            throw new RegraDeNegocioException("Codigo invalido ou expirado");
        }

        usuario.limparCodigo();
        usuario.completarCadastro(nome, celular);
        repository.save(usuario);

        String token = jwtService.gerarToken(usuario.getId(), usuario.getPapel());
        return new AuthResultado(usuario, token);
    }

    private String gerarCodigo() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
