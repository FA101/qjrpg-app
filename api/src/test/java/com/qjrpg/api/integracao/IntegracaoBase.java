package com.qjrpg.api.integracao;

import com.jayway.jsonpath.JsonPath;
import com.qjrpg.api.auth.seguranca.JwtService;
import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.evento.EventoRepository;
import com.qjrpg.api.evento.StatusEvento;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Base dos testes de integracao: sobe a aplicacao inteira (controllers, services,
 * JPA, filtro JWT e regras de autorizacao) contra um H2 em memoria no modo PostgreSQL.
 * As requisicoes passam pela mesma cadeia de seguranca usada em producao.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:qjrpg_integracao;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "app.jwt.secret=segredo-exclusivo-dos-testes-de-integracao-0123456789",
        "app.admin-emails=",
        "app.auth.expor-codigo=true"
})
abstract class IntegracaoBase {

    @Autowired protected WebApplicationContext contexto;
    @Autowired protected UsuarioRepository usuarioRepository;
    @Autowired protected EventoRepository eventoRepository;
    @Autowired protected JwtService jwtService;

    protected MockMvc mvc;

    @BeforeEach
    void montarMockMvcComSeguranca() {
        Filter cadeiaDeSeguranca = contexto.getBean("springSecurityFilterChain", Filter.class);
        mvc = MockMvcBuilders.webAppContextSetup(contexto).addFilters(cadeiaDeSeguranca).build();
    }

    // ---------- dados de teste

    protected Usuario criarUsuario(PapelUsuario papel) {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        Usuario u = new Usuario("u" + sufixo + "@teste.qjrpg", papel);
        u.definirApelido("Jogador" + sufixo);
        return usuarioRepository.save(u);
    }

    protected Usuario criarMenorPendente() {
        String sufixo = UUID.randomUUID().toString().substring(0, 8);
        Usuario u = new Usuario("menor" + sufixo + "@teste.qjrpg", PapelUsuario.COMUM);
        u.completarCadastro("Menor", null, "Menor" + sufixo, LocalDate.now().minusYears(12), null,
                "responsavel" + sufixo + "@teste.qjrpg");
        return usuarioRepository.save(u);
    }

    protected String token(Usuario u) {
        return "Bearer " + jwtService.gerarToken(u.getId(), u.getPapel());
    }

    protected Evento criarEvento() {
        return eventoRepository.save(new Evento("QJRPG Teste " + UUID.randomUUID(), LocalDate.now().plusDays(30),
                "HUB Goias", null, StatusEvento.PUBLICADO, LocalTime.of(9, 0), LocalTime.of(22, 0)));
    }

    // ---------- chamadas HTTP reutilizadas

    protected ResultActions ofertarMesa(Usuario gm, UUID eventoId, String inicio, String fim, int vagas) throws Exception {
        String corpo = """
                {"eventoId":"%s","tipoJogo":"RPG","sistemaJogo":"D&D 2024","tituloAventura":"Teste",
                 "horaInicio":"%s","horaFim":"%s","vagasTotais":%d,"vagasReservadas":0}
                """.formatted(eventoId, inicio, fim, vagas);
        return mvc.perform(post("/api/mesas").header("Authorization", token(gm))
                .contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    protected String mesaAprovada(Usuario gm, Usuario admin, UUID eventoId, String inicio, String fim, int vagas)
            throws Exception {
        String mesaId = idDe(ofertarMesa(gm, eventoId, inicio, fim, vagas).andReturn());
        mvc.perform(patch("/api/mesas/{id}/status", mesaId).header("Authorization", token(admin))
                .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"APROVADA\"}"));
        return mesaId;
    }

    protected ResultActions candidatar(Usuario jogador, String mesaId) throws Exception {
        return mvc.perform(post("/api/candidaturas").header("Authorization", token(jogador))
                .contentType(MediaType.APPLICATION_JSON).content("{\"mesaId\":\"" + mesaId + "\"}"));
    }

    protected ResultActions aceitar(Usuario quem, String candidaturaId) throws Exception {
        return mvc.perform(patch("/api/candidaturas/{id}/aceitar", candidaturaId).header("Authorization", token(quem)));
    }

    protected ResultActions listarCandidaturas(Usuario quem, String mesaId) throws Exception {
        return mvc.perform(get("/api/candidaturas").param("mesaId", mesaId).header("Authorization", token(quem)));
    }

    protected static String idDe(MvcResult resultado) throws Exception {
        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");
    }
}
