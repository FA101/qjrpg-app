package com.qjrpg.api.auth;

import com.qjrpg.api.auth.seguranca.JwtService;
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

    private AuthService service;

    @BeforeEach
    void configurar() {
        service = new AuthServiceImpl(repository, jwtService, filtro, "admin@qjrpg.com");
        lenient().when(filtro.contemPalavraProibida(any())).thenReturn(false);
    }

    @Test
    void deveGerarCodigoDeSeisDigitos() {
        when(repository.findByEmail("jogador@exemplo.com")).thenReturn(Optional.empty());
        when(repository.save(any(Usuario.class))).thenAnswer(c -> c.getArgument(0));

        String codigo = service.solicitarCodigo("jogador@exemplo.com");

        assertThat(codigo).hasSize(6);
    }

    @Test
    void deveExigirApelidoNoPrimeiroAcesso() {
        Usuario usuario = new Usuario("jogador@exemplo.com", PapelUsuario.COMUM);
        usuario.definirCodigo("123456", Instant.now().plusSeconds(60));
        when(repository.findByEmail("jogador@exemplo.com")).thenReturn(Optional.of(usuario));

        assertThrows(RegraDeNegocioException.class, () ->
                service.confirmarCodigo("jogador@exemplo.com", "123456", "Fabio", "62999999999", null));
    }

    @Test
    void deveRecusarApelidoComTermoProibido() {
        Usuario usuario = new Usuario("jogador@exemplo.com", PapelUsuario.COMUM);
        usuario.definirCodigo("123456", Instant.now().plusSeconds(60));
        when(repository.findByEmail("jogador@exemplo.com")).thenReturn(Optional.of(usuario));
        when(filtro.contemPalavraProibida("ApelidoRuim")).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () ->
                service.confirmarCodigo("jogador@exemplo.com", "123456", "Fabio", null, "ApelidoRuim"));
    }

    @Test
    void deveConfirmarCadastroCompletoComSucesso() {
        Usuario usuario = new Usuario("jogador@exemplo.com", PapelUsuario.COMUM);
        usuario.definirCodigo("123456", Instant.now().plusSeconds(60));
        when(repository.findByEmail("jogador@exemplo.com")).thenReturn(Optional.of(usuario));
        when(repository.findByApelido("Fabio62")).thenReturn(Optional.empty());
        when(repository.save(any(Usuario.class))).thenAnswer(c -> c.getArgument(0));
        when(jwtService.gerarToken(any(), any())).thenReturn("token-fake");

        AuthResultado resultado = service.confirmarCodigo("jogador@exemplo.com", "123456", "Fabio", "62999999999", "Fabio62");

        assertThat(resultado.token()).isEqualTo("token-fake");
        assertThat(resultado.usuario().getApelido()).isEqualTo("Fabio62");
    }
}
