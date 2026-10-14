package com.qjrpg.api.integracao;

import com.jayway.jsonpath.JsonPath;
import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Casos gerados com o PROMPT TIPO 2 (contrato da API e casos de erro/seguranca).
 * Verificam o "acordo" entre o app Flutter e a API: codigos HTTP, formato do corpo
 * de erro ({"erro": ...} ou {campo: mensagem}) e regras de permissao por papel.
 * Ver docs/testes-integracao/PROMPTS_E_RESULTADOS.md
 */
@DisplayName("Integracao: contrato da API, erros e permissoes")
class ContratoEErrosApiIntegracaoTest extends IntegracaoBase {

    @Test
    @DisplayName("CE-01 Rota protegida sem token e recusada")
    void semTokenERecusado() throws Exception {
        mvc.perform(get("/api/eventos")).andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("CE-02 Token adulterado e tratado como anonimo")
    void tokenInvalido() throws Exception {
        mvc.perform(get("/api/eventos").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("CE-03 Regras do evento e links uteis sao publicos (RF52, RF56)")
    void rotasPublicas() throws Exception {
        mvc.perform(get("/api/conteudos")).andExpect(status().isOk());
        mvc.perform(get("/api/links-uteis")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("CE-04 Validacao devolve 400 com o nome do campo invalido")
    void validacaoDeCampos() throws Exception {
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        mvc.perform(post("/api/mesas").header("Authorization", token(gm))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoJogo\":\"\",\"vagasTotais\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.eventoId").exists())
                .andExpect(jsonPath("$.tipoJogo").exists());
    }

    @Test
    @DisplayName("CE-05 Regra de negocio devolve 422 com {erro}: mesa fora da janela do evento (RF49)")
    void mesaForaDaJanela() throws Exception {
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        ofertarMesa(gm, criarEvento().getId(), "07:00:00", "10:00:00", 4)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("CE-06 Mesmo GM nao oferta duas mesas sobrepostas no evento (RF48)")
    void ofertaSobreposta() throws Exception {
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        Evento evento = criarEvento();
        ofertarMesa(gm, evento.getId(), "09:00:00", "13:00:00", 4).andExpect(status().isCreated());
        ofertarMesa(gm, evento.getId(), "12:00:00", "15:00:00", 4).andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("CE-07 Recurso inexistente devolve 404 com {erro}")
    void recursoInexistente() throws Exception {
        Usuario u = criarUsuario(PapelUsuario.COMUM);
        mvc.perform(get("/api/mesas/{id}", UUID.randomUUID()).header("Authorization", token(u)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("CE-08 Usuario comum nao aprova mesa (RF08) e nao cria evento (RF04)")
    void usuarioComumSemPermissao() throws Exception {
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        String mesaId = idDe(ofertarMesa(gm, criarEvento().getId(), "09:00:00", "13:00:00", 4).andReturn());

        mvc.perform(patch("/api/mesas/{id}/status", mesaId).header("Authorization", token(gm))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"APROVADA\"}"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/eventos").header("Authorization", token(gm))
                        .contentType(MediaType.APPLICATION_JSON)
                        // corpo valido: a recusa tem de vir da permissao, nao da validacao
                        .content("""
                                {"nome":"X","data":"2026-12-05","local":"Y","status":"PLANEJADO",
                                 "horaInicioJanela":"09:00:00","horaFimJanela":"22:00:00"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CE-09 Usuario que nao e GM da mesa nao aceita candidatura (RF11) e recebe 403 com {erro}")
    void terceiroNaoAceitaCandidatura() throws Exception {
        Usuario admin = criarUsuario(PapelUsuario.ADMIN);
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador = criarUsuario(PapelUsuario.COMUM);
        Usuario intruso = criarUsuario(PapelUsuario.COMUM);
        String mesa = mesaAprovada(gm, admin, criarEvento().getId(), "09:00:00", "13:00:00", 4);
        String cand = idDe(candidatar(jogador, mesa).andReturn());

        aceitar(intruso, cand)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("CE-10 usuarioId enviado no corpo e ignorado: vale o usuario do token")
    void naoPermiteCandidaturaEmNomeDeOutro() throws Exception {
        Usuario admin = criarUsuario(PapelUsuario.ADMIN);
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador = criarUsuario(PapelUsuario.COMUM);
        Usuario vitima = criarUsuario(PapelUsuario.COMUM);
        String mesa = mesaAprovada(gm, admin, criarEvento().getId(), "09:00:00", "13:00:00", 4);

        mvc.perform(post("/api/candidaturas").header("Authorization", token(jogador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mesaId\":\"" + mesa + "\",\"usuarioId\":\"" + vitima.getId() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(jogador.getId().toString()));
    }

    @Test
    @DisplayName("CE-11 Candidatura em mesa ainda pendente de aprovacao e recusada (RF10)")
    void candidaturaEmMesaPendente() throws Exception {
        Usuario gm = criarUsuario(PapelUsuario.COMUM);
        Usuario jogador = criarUsuario(PapelUsuario.COMUM);
        String mesaId = idDe(ofertarMesa(gm, criarEvento().getId(), "09:00:00", "13:00:00", 4).andReturn());

        candidatar(jogador, mesaId).andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("CE-12 Menor sem autorizacao do responsavel so acessa o proprio perfil (RF35)")
    void menorPendenteTemAcessoRestrito() throws Exception {
        Usuario menor = criarMenorPendente();

        mvc.perform(get("/api/usuarios/me").header("Authorization", token(menor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aguardandoAutorizacaoResponsavel").value(true));
        mvc.perform(get("/api/eventos").header("Authorization", token(menor)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CE-13 Contrato do login sem senha: solicitar codigo, confirmar e usar o token")
    void fluxoDeLoginContratoFrontEnd() throws Exception {
        String email = "novo" + UUID.randomUUID().toString().substring(0, 8) + "@teste.qjrpg";

        String resposta = mvc.perform(post("/api/auth/solicitar-codigo")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoParaTeste").isString())
                .andReturn().getResponse().getContentAsString();
        String codigo = JsonPath.read(resposta, "$.codigoParaTeste");

        String apelido = "Novo" + UUID.randomUUID().toString().substring(0, 6);
        String login = mvc.perform(post("/api/auth/confirmar-codigo").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"codigo\":\"%s\",\"apelido\":\"%s\"}".formatted(email, codigo, apelido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.totpPendente").value(false))
                .andExpect(jsonPath("$.papel").value("COMUM"))
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(login, "$.token");

        mvc.perform(get("/api/usuarios/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apelido").value(apelido));
    }

    @Test
    @DisplayName("CE-14 Codigo de login errado devolve 422 e nao emite token")
    void codigoErrado() throws Exception {
        String email = "erro" + UUID.randomUUID().toString().substring(0, 8) + "@teste.qjrpg";
        mvc.perform(post("/api/auth/solicitar-codigo")
                .contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"" + email + "\"}"));

        mvc.perform(post("/api/auth/confirmar-codigo").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"codigo\":\"000000\",\"apelido\":\"Qualquer1\"}".formatted(email)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.token").doesNotExist());
    }
}
