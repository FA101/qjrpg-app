package com.qjrpg.api.auth;

import com.qjrpg.api.auth.seguranca.JwtService;
import com.qjrpg.api.auth.seguranca.TotpService;
import com.qjrpg.api.moderacao.FiltroDePalavrasService;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UsuarioRepository repository;
    @Mock private JwtService jwtService;
    @Mock private FiltroDePalavrasService filtro;
    @Mock private TotpService totpService;

    private AuthService service;

    @BeforeEach
    void configurar() {
        service = new AuthServiceImpl(repository, jwtService, filtro, totpService, "admin@qjrpg.com");
        lenient().when(filtro.contemPalavraProibida(any())).thenReturn(false);
    }

    @Test
    void deveGerarCodigoDeSeisDigitos() {
        when(repository.findByEmail("jogador@exemplo.com")).thenReturn(Optional.empty());
        when(repository.save(any(Usuario.class))).thenAnswer(c -> c.getArgument(0));

        assertThat(service.solicitarCodigo("jogador@exemplo.com")).hasSize(6);
    }

    @Test
    void deveExigirResponsavelParaMenorDeIdade() {
        Usuario usuario = new Usuario("crianca@exemplo.com", PapelUsuario.COMUM);
        usuario.definirCodigo("123456", Instant.now().plusSeconds(60));
        when(repository.findByEmail("crianca@exemplo.com")).thenReturn(Optional.of(usuario));
        when(repository.findByApelido("Crianca10")).thenReturn(Optional.empty());

        assertThrows(RegraDeNegocioException.class, () -> service.confirmarCodigo(
                "crianca@exemplo.com", "123456", "Crianca", null, "Crianca10",
                LocalDate.now().minusYears(10), null, null));
    }

    @Test
    void deveConfirmarCadastroDeAdultoSemResponsavel() {
        Usuario usuario = new Usuario("jogador@exemplo.com", PapelUsuario.COMUM);
        usuario.definirCodigo("123456", Instant.now().plusSeconds(60));
        when(repository.findByEmail("jogador@exemplo.com")).thenReturn(Optional.of(usuario));
        when(repository.findByApelido("Fabio62")).thenReturn(Optional.empty());
        when(repository.save(any(Usuario.class))).thenAnswer(c -> c.getArgument(0));
        when(jwtService.gerarToken(any(), any())).thenReturn("token-fake");

        AuthResultado resultado = service.confirmarCodigo("jogador@exemplo.com", "123456", "Fabio", "62999999999",
                "Fabio62", LocalDate.now().minusYears(30), "M", null);

        assertThat(resultado.token()).isEqualTo("token-fake");
    }

    @Test
    void deveRetornarTokenNuloQuandoTotpEstaAtivo() {
        Usuario usuario = new Usuario("comdoisfatores@exemplo.com", PapelUsuario.COMUM);
        usuario.definirCodigo("123456", Instant.now().plusSeconds(60));
        usuario.definirApelido("JaTemApelido");
        usuario.definirTotpSecretPendente("SEGREDO");
        usuario.ativarTotp();
        when(repository.findByEmail("comdoisfatores@exemplo.com")).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(c -> c.getArgument(0));

        AuthResultado resultado = service.confirmarCodigo("comdoisfatores@exemplo.com", "123456", null, null,
                null, null, null, null);

        assertThat(resultado.token()).isNull();
    }

    private Usuario usuarioComTotpEDesafio(String desafio) {
        Usuario usuario = new Usuario("comdoisfatores@exemplo.com", PapelUsuario.COMUM);
        usuario.definirTotpSecretPendente("SEGREDO");
        usuario.ativarTotp();
        usuario.definirDesafio2fa(desafio, Instant.now().plusSeconds(120));
        when(repository.findByEmail("comdoisfatores@exemplo.com")).thenReturn(Optional.of(usuario));
        return usuario;
    }

    @Test
    void deveValidarSegundoFatorEEmitirToken() {
        usuarioComTotpEDesafio("desafio-ok");
        when(totpService.validarCodigo("SEGREDO", "111111")).thenReturn(true);
        when(repository.save(any(Usuario.class))).thenAnswer(c -> c.getArgument(0));
        when(jwtService.gerarToken(any(), any())).thenReturn("token-final");

        AuthResultado resultado = service.validarSegundoFator("comdoisfatores@exemplo.com", "desafio-ok", "111111");

        assertThat(resultado.token()).isEqualTo("token-final");
    }

    @Test
    void naoAceitaSegundoFatorSemDesafioValido() {
        usuarioComTotpEDesafio("desafio-ok");

        assertThrows(RegraDeNegocioException.class, () ->
                service.validarSegundoFator("comdoisfatores@exemplo.com", "desafio-forjado", "111111"));
    }

    @Test
    void confirmacaoComTotpDevolveDesafio() {
        Usuario usuario = new Usuario("comdoisfatores@exemplo.com", PapelUsuario.COMUM);
        usuario.definirCodigo("123456", Instant.now().plusSeconds(60));
        usuario.definirApelido("JaTemApelido");
        usuario.definirTotpSecretPendente("SEGREDO");
        usuario.ativarTotp();
        when(repository.findByEmail("comdoisfatores@exemplo.com")).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(c -> c.getArgument(0));

        AuthResultado resultado = service.confirmarCodigo("comdoisfatores@exemplo.com", "123456", null, null,
                null, null, null, null);

        assertThat(resultado.desafio2fa()).isNotBlank();
        assertThat(usuario.desafio2faValido(resultado.desafio2fa())).isTrue();
    }

    @Test
    void codigoDeEmailInvalidaAposCincoTentativasErradas() {
        Usuario usuario = new Usuario("jogador@exemplo.com", PapelUsuario.COMUM);
        usuario.definirCodigo("123456", Instant.now().plusSeconds(60));
        when(repository.findByEmail("jogador@exemplo.com")).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenAnswer(c -> c.getArgument(0));

        for (int i = 0; i < Usuario.MAX_TENTATIVAS; i++) {
            assertThrows(RegraDeNegocioException.class, () -> service.confirmarCodigo("jogador@exemplo.com",
                    "000000", "Fabio", null, "Fabio62", null, null, null));
        }

        assertThat(usuario.codigoValido("123456")).isFalse(); // codigo correto nao vale mais
    }
}
