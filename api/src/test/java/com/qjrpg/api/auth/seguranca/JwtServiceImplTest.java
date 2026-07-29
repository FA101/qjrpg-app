package com.qjrpg.api.auth.seguranca;

import com.qjrpg.api.usuario.PapelUsuario;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste sem mocks de proposito: valida a biblioteca jjwt de verdade
 * (gerar + validar token), sem depender de servidor rodando.
 * Se este teste falhar, o problema e a versao/API da biblioteca, nao a logica.
 */
class JwtServiceImplTest {

    private final JwtService jwtService = new JwtServiceImpl(
            "segredo-de-teste-com-pelo-menos-32-bytes-1234567890", 30);

    @Test
    void deveGerarEValidarTokenCorretamente() {
        UUID usuarioId = UUID.randomUUID();

        String token = jwtService.gerarToken(usuarioId, PapelUsuario.ADMIN);
        UUID extraido = jwtService.extrairUsuarioId(token);

        assertThat(extraido).isEqualTo(usuarioId);
    }
}
