package com.qjrpg.api.auth.seguranca;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste sem mocks de proposito, igual fizemos com o JWT: gera um codigo de
 * verdade com a mesma biblioteca (simulando o que um app autenticador faria)
 * e verifica se o service reconhece como valido. Se isto falhar, o problema
 * e a API da biblioteca dev.samstevens.totp, nao a logica do nosso codigo.
 */
class TotpServiceImplTest {

    private final TotpService totpService = new TotpServiceImpl();

    @Test
    void deveGerarUmSegredoNaoNuloEDiferenteACadaChamada() {
        String segredo1 = totpService.gerarSegredo();
        String segredo2 = totpService.gerarSegredo();

        assertThat(segredo1).isNotBlank();
        assertThat(segredo1).isNotEqualTo(segredo2);
    }

    @Test
    void deveValidarUmCodigoGeradoAgoraParaOMesmoSegredo() throws Exception {
        String segredo = totpService.gerarSegredo();
        SystemTimeProvider timeProvider = new SystemTimeProvider();
        long contador = Math.floorDiv(timeProvider.getTime(), 30);
        String codigo = new DefaultCodeGenerator().generate(segredo, contador);

        assertThat(totpService.validarCodigo(segredo, codigo)).isTrue();
    }

    @Test
    void deveRecusarCodigoErrado() {
        String segredo = totpService.gerarSegredo();
        assertThat(totpService.validarCodigo(segredo, "000000")).isFalse();
    }
}
