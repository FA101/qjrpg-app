package com.qjrpg.api.candidatura;

import com.qjrpg.api.candidatura.dto.CandidaturaRequest;
import com.qjrpg.api.mesa.Mesa;
import com.qjrpg.api.mesa.MesaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidaturaServiceImplTest {

    @Mock private CandidaturaRepository candidaturaRepository;
    @Mock private MesaRepository mesaRepository;
    private CandidaturaService service;

    private final UUID usuarioId = UUID.randomUUID();
    private final UUID eventoId = UUID.randomUUID();

    @BeforeEach
    void configurar() {
        service = new CandidaturaServiceImpl(candidaturaRepository, mesaRepository);
    }

    @Test
    void deveBloquearCandidaturaConflitanteAoAceitarOutra() {
        UUID mesaAId = UUID.randomUUID();
        UUID mesaBId = UUID.randomUUID();
        Mesa mesaA = new Mesa(eventoId, UUID.randomUUID(), null, "RPG", null, null, null, null, null, null,
                LocalTime.of(9, 0), LocalTime.of(13, 0), 5, 0);
        Mesa mesaB = new Mesa(eventoId, UUID.randomUUID(), null, "Boardgame", null, null, null, null, null, null,
                LocalTime.of(11, 0), LocalTime.of(15, 0), 4, 0);

        Candidatura candidaturaAceita = new Candidatura(mesaAId, usuarioId);
        Candidatura candidaturaConflitante = new Candidatura(mesaBId, usuarioId);

        when(candidaturaRepository.findById(candidaturaAceita.getId())).thenReturn(Optional.of(candidaturaAceita));
        when(mesaRepository.findById(mesaAId)).thenReturn(Optional.of(mesaA));
        when(mesaRepository.findById(mesaBId)).thenReturn(Optional.of(mesaB));
        when(candidaturaRepository.findByUsuarioIdAndStatus(usuarioId, StatusCandidatura.PENDENTE))
                .thenReturn(List.of(candidaturaConflitante));
        when(candidaturaRepository.save(any(Candidatura.class))).thenAnswer(c -> c.getArgument(0));

        service.aceitar(candidaturaAceita.getId());

        assertThat(candidaturaConflitante.isBloqueada()).isTrue();
        assertThat(candidaturaAceita.getStatus()).isEqualTo(StatusCandidatura.ACEITA);
    }

    @Test
    void deveCandidatarSemErro() {
        when(candidaturaRepository.save(any(Candidatura.class))).thenAnswer(c -> c.getArgument(0));
        Candidatura c = service.candidatar(new CandidaturaRequest(UUID.randomUUID(), usuarioId));
        assertThat(c.getStatus()).isEqualTo(StatusCandidatura.PENDENTE);
    }
}