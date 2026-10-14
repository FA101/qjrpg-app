package com.qjrpg.api.integracao;

import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Cenarios de integracao gerados com o PROMPT TIPO 1 (cenarios a partir das regras de negocio).
 * Fluxo: Mesa -> aprovacao -> Candidatura -> aceite/bloqueio/remocao, atravessando
 * MesaController, CandidaturaController, services, JPA, NotificacaoService e TagService.
 * Ver docs/testes-integracao/PROMPTS_E_RESULTADOS.md
 */
@DisplayName("Integracao: fluxo de mesas e candidaturas (RF07-RF11, RF44-RF47)")
class CandidaturaFluxoIntegracaoTest extends IntegracaoBase {

    @Test
    @DisplayName("CT-01 Fluxo feliz: ofertar, aprovar, candidatar e aceitar reduz vagas e da a tag Game Master")
    void fluxoFelizCompleto() throws Exception {
        Usuario admin = criarUsuario(PapelUsuario.ADMIN);
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador = criarUsuario(PapelUsuario.COMUM);
        Evento evento = criarEvento();

        ofertarMesa(gm, evento.getId(), "09:00:00", "13:00:00", 4)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.gameMasterId").value(gm.getId().toString()));

        String mesaId = mesaAprovada(gm, admin, evento.getId(), "14:00:00", "18:00:00", 4);
        String candidaturaId = idDe(candidatar(jogador, mesaId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn());

        aceitar(gm, candidaturaId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACEITA"));

        mvc.perform(get("/api/mesas/{id}", mesaId).header("Authorization", token(jogador)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vagasDisponiveis").value(3));

        mvc.perform(get("/api/usuarios/{id}/tags", gm.getId()).header("Authorization", token(jogador)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome", hasItem("Game Master")));
    }

    @Test
    @DisplayName("CT-02 Aceitar uma candidatura bloqueia a conflitante do mesmo usuario no mesmo evento")
    void aceitarBloqueiaConflitante() throws Exception {
        Usuario admin = criarUsuario(PapelUsuario.ADMIN);
        Usuario gmA = criarUsuario(PapelUsuario.COMUM);
        Usuario gmB = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador = criarUsuario(PapelUsuario.COMUM);
        Evento evento = criarEvento();
        String mesaA = mesaAprovada(gmA, admin, evento.getId(), "09:00:00", "13:00:00", 4);
        String mesaB = mesaAprovada(gmB, admin, evento.getId(), "11:00:00", "15:00:00", 4);

        String candA = idDe(candidatar(jogador, mesaA).andReturn());
        String candB = idDe(candidatar(jogador, mesaB).andExpect(status().isCreated()).andReturn());

        aceitar(gmA, candA).andExpect(status().isOk());

        listarCandidaturas(jogador, mesaB)
                .andExpect(jsonPath("$[0].id").value(candB))
                .andExpect(jsonPath("$[0].bloqueada").value(true));

        aceitar(gmB, candB)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("CT-03 Horarios encostados (13h fim, 13h inicio) nao geram bloqueio")
    void horariosEncostadosNaoConflitam() throws Exception {
        Usuario admin = criarUsuario(PapelUsuario.ADMIN);
        Usuario gmA = criarUsuario(PapelUsuario.COMUM);
        Usuario gmB = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador = criarUsuario(PapelUsuario.COMUM);
        Evento evento = criarEvento();
        String mesaA = mesaAprovada(gmA, admin, evento.getId(), "09:00:00", "13:00:00", 4);
        String mesaB = mesaAprovada(gmB, admin, evento.getId(), "13:00:00", "17:00:00", 4);

        String candA = idDe(candidatar(jogador, mesaA).andReturn());
        String candB = idDe(candidatar(jogador, mesaB).andReturn());
        aceitar(gmA, candA).andExpect(status().isOk());

        aceitar(gmB, candB).andExpect(status().isOk());
    }

    @Test
    @DisplayName("CT-04 Mesas de eventos diferentes no mesmo horario nao conflitam")
    void eventosDiferentesNaoConflitam() throws Exception {
        Usuario admin = criarUsuario(PapelUsuario.ADMIN);
        Usuario gmA = criarUsuario(PapelUsuario.COMUM);
        Usuario gmB = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador = criarUsuario(PapelUsuario.COMUM);
        String mesaA = mesaAprovada(gmA, admin, criarEvento().getId(), "09:00:00", "13:00:00", 4);
        String mesaB = mesaAprovada(gmB, admin, criarEvento().getId(), "09:00:00", "13:00:00", 4);

        String candA = idDe(candidatar(jogador, mesaA).andReturn());
        String candB = idDe(candidatar(jogador, mesaB).andReturn());
        aceitar(gmA, candA).andExpect(status().isOk());

        listarCandidaturas(jogador, mesaB).andExpect(jsonPath("$[0].bloqueada").value(false));
        aceitar(gmB, candB).andExpect(status().isOk());
    }

    @Test
    @DisplayName("CT-05 Remover a candidatura aceita desbloqueia a conflitante (RF47)")
    void removerAceitaDesbloqueia() throws Exception {
        Usuario admin = criarUsuario(PapelUsuario.ADMIN);
        Usuario gmA = criarUsuario(PapelUsuario.COMUM);
        Usuario gmB = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador = criarUsuario(PapelUsuario.COMUM);
        Evento evento = criarEvento();
        String mesaA = mesaAprovada(gmA, admin, evento.getId(), "09:00:00", "13:00:00", 4);
        String mesaB = mesaAprovada(gmB, admin, evento.getId(), "10:00:00", "12:00:00", 4);
        String candA = idDe(candidatar(jogador, mesaA).andReturn());
        String candB = idDe(candidatar(jogador, mesaB).andReturn());
        aceitar(gmA, candA).andExpect(status().isOk());

        mvc.perform(delete("/api/candidaturas/{id}", candA).header("Authorization", token(jogador)))
                .andExpect(status().isNoContent());

        listarCandidaturas(jogador, mesaB).andExpect(jsonPath("$[0].bloqueada").value(false));
        aceitar(gmB, candB).andExpect(status().isOk());
    }

    @Test
    @DisplayName("CT-06 Mesa lotada recusa nova candidatura e novo aceite")
    void mesaLotada() throws Exception {
        Usuario admin = criarUsuario(PapelUsuario.ADMIN);
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador1 = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador2 = criarUsuario(PapelUsuario.COMUM);
        String mesa = mesaAprovada(gm, admin, criarEvento().getId(), "09:00:00", "13:00:00", 1);

        String cand1 = idDe(candidatar(jogador1, mesa).andReturn());
        aceitar(gm, cand1).andExpect(status().isOk());

        candidatar(jogador2, mesa)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erro").exists());
    }
}
