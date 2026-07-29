package com.qjrpg.api.mesa;

import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.evento.EventoRepository;
import com.qjrpg.api.evento.StatusEvento;
import com.qjrpg.api.mesa.dto.MesaRequest;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MesaServiceImplTest {

    @Mock private MesaRepository mesaRepository;
    @Mock private EventoRepository eventoRepository;
    private MesaService service;

    private final UUID eventoId = UUID.randomUUID();
    private final UUID gmId = UUID.randomUUID();

    @BeforeEach
    void configurar() { service = new MesaServiceImpl(mesaRepository, eventoRepository); }

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

        Mesa mesa = service.ofertar(requestPadrao(1, LocalTime.of(18, 0), LocalTime.of(22, 0)));

        assertThat(mesa.getHoraInicio()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    void deveRecusarSobreposicaoDeHorario() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));
        Mesa mesaExistente = new Mesa(eventoId, gmId, 1, "RPG", "D&D 2024", null, null, null, null, "Livre",
                LocalTime.of(9, 0), LocalTime.of(14, 0), 5, 0);
        when(mesaRepository.findByEventoId(eventoId)).thenReturn(List.of(mesaExistente));

        assertThrows(RegraDeNegocioException.class, () ->
                service.ofertar(requestPadrao(2, LocalTime.of(13, 0), LocalTime.of(17, 0))));
    }

    @Test
    void deveRecusarForaDaJanelaDoEvento() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));

        assertThrows(RegraDeNegocioException.class, () ->
                service.ofertar(requestPadrao(1, LocalTime.of(7, 0), LocalTime.of(9, 30))));
    }

    @Test
    void deveRecusarNumeroDuplicadoNoMesmoEvento() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));
        Mesa mesaExistente = new Mesa(eventoId, UUID.randomUUID(), 7, "RPG", null, null, null, null, null, "Livre",
                LocalTime.of(9, 0), LocalTime.of(13, 0), 5, 0);
        when(mesaRepository.findByEventoId(eventoId)).thenReturn(List.of(mesaExistente));

        assertThrows(RegraDeNegocioException.class, () ->
                service.ofertar(requestPadrao(7, LocalTime.of(14, 0), LocalTime.of(18, 0))));
    }
}
