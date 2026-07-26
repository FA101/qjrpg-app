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
    void configurar() {
        service = new MesaServiceImpl(mesaRepository, eventoRepository);
    }

    private Evento eventoComJanela() {
        return new Evento("QJRPG", "HUB Goias", null, StatusEvento.PUBLICADO,
                LocalTime.of(9, 0), LocalTime.of(22, 0));
    }

    @Test
    void devePermitirHorariosEncostados() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));
        when(mesaRepository.findByEventoIdAndGameMasterId(eventoId, gmId)).thenReturn(List.of());
        when(mesaRepository.save(any(Mesa.class))).thenAnswer(c -> c.getArgument(0));

        // mesa das 14h-18h, nova mesa 18h-22h: nao deve lancar excecao
        MesaRequest r = new MesaRequest(eventoId, gmId, "RPG", LocalTime.of(18, 0), LocalTime.of(22, 0), 5);
        Mesa mesa = service.ofertar(r);

        assertThat(mesa.getHoraInicio()).isEqualTo(LocalTime.of(18, 0));
    }

    @Test
    void deveRecusarSobreposicaoDeHorario() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));
        Mesa mesaExistente = new Mesa(eventoId, gmId, "RPG", LocalTime.of(9, 0), LocalTime.of(14, 0), 5);
        when(mesaRepository.findByEventoIdAndGameMasterId(eventoId, gmId)).thenReturn(List.of(mesaExistente));

        MesaRequest r = new MesaRequest(eventoId, gmId, "Boardgame", LocalTime.of(13, 0), LocalTime.of(17, 0), 4);

        assertThrows(RegraDeNegocioException.class, () -> service.ofertar(r));
    }

    @Test
    void deveRecusarForaDaJanelaDoEvento() {
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComJanela()));

        MesaRequest r = new MesaRequest(eventoId, gmId, "Wargame", LocalTime.of(7, 0), LocalTime.of(9, 30), 3);

        assertThrows(RegraDeNegocioException.class, () -> service.ofertar(r));
    }
}
