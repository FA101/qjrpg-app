package com.qjrpg.api.mesa;

import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.evento.EventoRepository;
import com.qjrpg.api.evento.StatusEvento;
import com.qjrpg.api.mesa.dto.MesaRequest;
import com.qjrpg.api.notificacao.NotificacaoService;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.tag.TagService;
import com.qjrpg.api.tag.TagSistema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MesaServiceImplTest {

    @Mock private MesaRepository mesaRepository;
    @Mock private EventoRepository eventoRepository;
    @Mock private NotificacaoService notificacaoService;
    @Mock private TagService tagService;
    private MesaService service;

    private final UUID eventoId = UUID.randomUUID();
    private final UUID gmId = UUID.randomUUID();

    @BeforeEach
    void configurar() { service = new MesaServiceImpl(mesaRepository, eventoRepository, notificacaoService, tagService); }

    private Evento eventoComJanela() {
        return new Evento("QJRPG", LocalDate.of(2026, 8, 15), "HUB Goias", null, StatusEvento.PUBLICADO,
                LocalTime.of(9, 0), LocalTime.of(22, 0));
    }

    private MesaRequest requestPadrao(Integer numero, LocalTime inicio, LocalTime fim) {
        return new MesaRequest(eventoId, gmId, numero, "RPG", "D&D 2024", "Titulo", "Sinopse",
                "palavra1,palavra2", "Observacoes", "Livre", inicio, fim, 5, 0);
    }

    @Test
    void devePermitirHorariosEncostados() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));
        when(mesaRepository.findByEventoId(eventoId)).thenReturn(List.of());
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(c -> c.getArgument(0));

        Mesa mesa = service.ofertar(requestPadrao(1, LocalTime.of(18, 0), LocalTime.of(22, 0)), gmId);

        assertThat(mesa.getHoraInicio()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    void deveRecusarSobreposicaoDeHorario() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));
        Mesa mesaExistente = new Mesa(eventoId, gmId, 1, "RPG", "D&D 2024", null, null, null, null, "Livre",
                LocalTime.of(9, 0), LocalTime.of(14, 0), 5, 0);
        when(mesaRepository.findByEventoId(eventoId)).thenReturn(List.of(mesaExistente));

        assertThrows(RegraDeNegocioException.class, () ->
                service.ofertar(requestPadrao(2, LocalTime.of(13, 0), LocalTime.of(17, 0)), gmId));
    }

    @Test
    void deveRecusarForaDaJanelaDoEvento() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));

        assertThrows(RegraDeNegocioException.class, () ->
                service.ofertar(requestPadrao(1, LocalTime.of(7, 0), LocalTime.of(9, 30)), gmId));
    }

    @Test
    void deveRecusarInicioDepoisDoFim() {
        assertThrows(RegraDeNegocioException.class, () ->
                service.ofertar(requestPadrao(1, LocalTime.of(15, 0), LocalTime.of(14, 0)), gmId));
    }

    @Test
    void deveUsarUsuarioAutenticadoComoGameMaster() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));
        when(mesaRepository.findByEventoId(eventoId)).thenReturn(List.of());
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(c -> c.getArgument(0));
        UUID outroUsuario = UUID.randomUUID();

        Mesa mesa = service.ofertar(requestPadrao(3, LocalTime.of(9, 0), LocalTime.of(12, 0)), outroUsuario);

        assertThat(mesa.getGameMasterId()).isEqualTo(outroUsuario);
    }

    @Test
    void aprovarMesaAtribuiTagGameMasterAoOfertante() { // RF09
        UUID mesaId = UUID.randomUUID();
        Mesa mesa = new Mesa(eventoId, gmId, 1, "RPG", null, null, null, null, null, "Livre",
                LocalTime.of(9, 0), LocalTime.of(13, 0), 5, 0);
        when(mesaRepository.findById(mesaId)).thenReturn(Optional.of(mesa));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(c -> c.getArgument(0));

        service.atualizarStatus(mesaId, StatusMesa.APROVADA);

        verify(tagService).atribuirTagDoSistema(gmId, TagSistema.GAME_MASTER);
    }

    @Test
    void recusarMesaNaoAtribuiTag() {
        UUID mesaId = UUID.randomUUID();
        Mesa mesa = new Mesa(eventoId, gmId, 1, "RPG", null, null, null, null, null, "Livre",
                LocalTime.of(9, 0), LocalTime.of(13, 0), 5, 0);
        when(mesaRepository.findById(mesaId)).thenReturn(Optional.of(mesa));
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(c -> c.getArgument(0));

        service.atualizarStatus(mesaId, StatusMesa.RECUSADA);

        verify(tagService, never()).atribuirTagDoSistema(any(), any());
    }

    @Test
    void deveRecusarNumeroDuplicadoNoMesmoEvento() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));
        Mesa mesaExistente = new Mesa(eventoId, UUID.randomUUID(), 7, "RPG", null, null, null, null, null, "Livre",
                LocalTime.of(9, 0), LocalTime.of(13, 0), 5, 0);
        when(mesaRepository.findByEventoId(eventoId)).thenReturn(List.of(mesaExistente));

        assertThrows(RegraDeNegocioException.class, () ->
                service.ofertar(requestPadrao(7, LocalTime.of(14, 0), LocalTime.of(18, 0)), gmId));
    }
}
