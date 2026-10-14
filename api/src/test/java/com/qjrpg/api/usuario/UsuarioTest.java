package com.qjrpg.api.usuario;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    private Usuario menorComResponsavel() {
        Usuario u = new Usuario("menor@exemplo.com", PapelUsuario.COMUM);
        u.completarCadastro("Menor", null, "Menor10", LocalDate.now().minusYears(12), null, "pai@exemplo.com");
        return u;
    }

    @Test
    void menorFicaPendenteAteResponsavelConfirmar() { // RF35
        Usuario u = menorComResponsavel();
        assertThat(u.aguardandoAutorizacaoResponsavel()).isTrue();

        u.confirmarVinculoResponsavel();

        assertThat(u.aguardandoAutorizacaoResponsavel()).isFalse();
    }

    @Test
    void revogacaoPeloResponsavelSuspendeAcessoImediatamente() { // RF36b
        Usuario u = menorComResponsavel();
        u.confirmarVinculoResponsavel();

        u.revogarVinculoResponsavel();

        assertThat(u.aguardandoAutorizacaoResponsavel()).isTrue();
        assertThat(u.getResponsavelEmail()).isEqualTo("pai@exemplo.com");
    }

    @Test
    void adultoNuncaFicaPendente() {
        Usuario u = new Usuario("adulto@exemplo.com", PapelUsuario.COMUM);
        u.completarCadastro("Adulto", null, "Adulto30", LocalDate.now().minusYears(30), null, null);
        assertThat(u.aguardandoAutorizacaoResponsavel()).isFalse();
    }
}
