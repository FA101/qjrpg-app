package com.qjrpg.api.candidatura;

import com.qjrpg.api.mesa.Mesa;
import com.qjrpg.api.mesa.MesaRepository;
import com.qjrpg.api.mesa.StatusMesa;
import com.qjrpg.api.notificacao.NotificacaoService;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidaturaServiceImplTest {

    @Mock private CandidaturaRepository candidaturaRepository;
    @Mock private MesaRepository mesaRepository;
    @Mock private NotificacaoService notificacaoService;
    private CandidaturaService service;

    private final UUID usuarioId = UUID.randomUUID();
    private final UUID eventoId = UUID.randomUUID();

    @BeforeEach
    void configurar() {
        service = new CandidaturaServiceImpl(candidaturaRepository, mesaRepository, notificacaoService);
    }

    private Mesa mesa(UUID evento, int hIni, int hFim, int vagas) {
        Mesa m = new Mesa(evento, UUID.randomUUID(), null, "RPG", null, null, null, null, null, null,
                LocalTime.of(hIni, 0), LocalTime.of(hFim, 0), vagas, 0);
        ReflectionTestUtils.setField(m, "id", UUID.randomUUID());
        m.atualizarStatus(StatusMesa.APROVADA);
        lenient().when(mesaRepository.findById(m.getId())).thenReturn(Optional.of(m));
        return m;
    }

    private Candidatura candidatura(Mesa m) {
        Candidatura c = new Candidatura(m.getId(), usuarioId);
        ReflectionTestUtils.setField(c, "id", UUID.randomUUID());
        return c;
    }

    private void salvarDevolveArgumento() {
        when(candidaturaRepository.save(any(Candidatura.class))).thenAnswer(c -> c.getArgument(0));
    }

    // ---------- aceitar (RF46)

    @Test
    void aceitarBloqueiaCandidaturaConflitanteDoMesmoEvento() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        Mesa mesaB = mesa(eventoId, 11, 15, 4);
        Candidatura aceita = candidatura(mesaA);
        Candidatura conflitante = candidatura(mesaB);
        when(candidaturaRepository.findById(aceita.getId())).thenReturn(Optional.of(aceita));
        when(candidaturaRepository.findByUsuarioIdAndStatus(usuarioId, StatusCandidatura.PENDENTE))
                .thenReturn(List.of(aceita, conflitante));
        salvarDevolveArgumento();

        service.aceitar(aceita.getId());

        assertThat(aceita.getStatus()).isEqualTo(StatusCandidatura.ACEITA);
        assertThat(conflitante.isBloqueada()).isTrue();
        verify(notificacaoService).notificarAdministracao(anyString());
    }

    @Test
    void aceitarNaoBloqueiaMesaDeOutroEventoNoMesmoHorario() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        Mesa mesaOutroEvento = mesa(UUID.randomUUID(), 9, 13, 5);
        Candidatura aceita = candidatura(mesaA);
        Candidatura outra = candidatura(mesaOutroEvento);
        when(candidaturaRepository.findById(aceita.getId())).thenReturn(Optional.of(aceita));
        when(candidaturaRepository.findByUsuarioIdAndStatus(usuarioId, StatusCandidatura.PENDENTE))
                .thenReturn(List.of(outra));
        salvarDevolveArgumento();

        service.aceitar(aceita.getId());

        assertThat(outra.isBloqueada()).isFalse();
    }

    @Test
    void naoAceitaCandidaturaBloqueada() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        Candidatura bloqueada = candidatura(mesaA);
        bloqueada.bloquear();
        when(candidaturaRepository.findById(bloqueada.getId())).thenReturn(Optional.of(bloqueada));

        assertThrows(RegraDeNegocioException.class, () -> service.aceitar(bloqueada.getId()));
        assertThat(bloqueada.getStatus()).isEqualTo(StatusCandidatura.PENDENTE);
    }

    @Test
    void naoAceitaQuandoMesaEstaLotada() {
        Mesa mesaA = mesa(eventoId, 9, 13, 2);
        Candidatura c = candidatura(mesaA);
        when(candidaturaRepository.findById(c.getId())).thenReturn(Optional.of(c));
        when(candidaturaRepository.countByMesaIdAndStatus(mesaA.getId(), StatusCandidatura.ACEITA)).thenReturn(2L);

        assertThrows(RegraDeNegocioException.class, () -> service.aceitar(c.getId()));
    }

    // ---------- candidatar (RF10, RF44, RF45)

    @Test
    void candidatarEmMesaAprovadaCriaPendente() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        when(candidaturaRepository.findByMesaIdAndUsuarioId(mesaA.getId(), usuarioId)).thenReturn(Optional.empty());
        when(candidaturaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of());
        salvarDevolveArgumento();

        Candidatura c = service.candidatar(mesaA.getId(), usuarioId);

        assertThat(c.getStatus()).isEqualTo(StatusCandidatura.PENDENTE);
        assertThat(c.isBloqueada()).isFalse();
        verify(notificacaoService, never()).notificarAdministracao(anyString());
    }

    @Test
    void naoPermiteCandidaturaEmMesaNaoAprovada() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        mesaA.atualizarStatus(StatusMesa.PENDENTE);

        assertThrows(RegraDeNegocioException.class, () -> service.candidatar(mesaA.getId(), usuarioId));
    }

    @Test
    void gameMasterNaoSeCandidataAPropriaMesa() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);

        assertThrows(RegraDeNegocioException.class, () -> service.candidatar(mesaA.getId(), mesaA.getGameMasterId()));
    }

    @Test
    void naoPermiteCandidaturaDuplicadaNaMesmaMesa() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        when(candidaturaRepository.findByMesaIdAndUsuarioId(mesaA.getId(), usuarioId))
                .thenReturn(Optional.of(candidatura(mesaA)));

        assertThrows(RegraDeNegocioException.class, () -> service.candidatar(mesaA.getId(), usuarioId));
    }

    @Test
    void candidaturaConflitantePermitidaENotificaUsuarioEAdministracao() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        Mesa mesaB = mesa(eventoId, 12, 16, 5);
        Candidatura existente = candidatura(mesaA);
        when(candidaturaRepository.findByMesaIdAndUsuarioId(mesaB.getId(), usuarioId)).thenReturn(Optional.empty());
        when(candidaturaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(existente));
        salvarDevolveArgumento();

        Candidatura nova = service.candidatar(mesaB.getId(), usuarioId);

        assertThat(nova.isBloqueada()).isFalse(); // a existente ainda esta pendente
        verify(notificacaoService).notificar(any(UUID.class), anyString());
        verify(notificacaoService).notificarAdministracao(anyString());
    }

    @Test
    void candidaturaConflitanteComUmaJaAceitaNasceBloqueada() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        Mesa mesaB = mesa(eventoId, 12, 16, 5);
        Candidatura aceita = candidatura(mesaA);
        aceita.aceitar();
        when(candidaturaRepository.findByMesaIdAndUsuarioId(mesaB.getId(), usuarioId)).thenReturn(Optional.empty());
        when(candidaturaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(aceita));
        salvarDevolveArgumento();

        Candidatura nova = service.candidatar(mesaB.getId(), usuarioId);

        assertThat(nova.isBloqueada()).isTrue();
    }

    @Test
    void horariosEncostadosNaoGeramConflito() {
        Mesa mesaA = mesa(eventoId, 9, 14, 5);
        Mesa mesaB = mesa(eventoId, 14, 18, 5);
        Candidatura existente = candidatura(mesaA);
        when(candidaturaRepository.findByMesaIdAndUsuarioId(mesaB.getId(), usuarioId)).thenReturn(Optional.empty());
        when(candidaturaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(existente));
        salvarDevolveArgumento();

        service.candidatar(mesaB.getId(), usuarioId);

        verify(notificacaoService, never()).notificarAdministracao(anyString());
    }

    // ---------- remover (RF47)

    @Test
    void removerCandidaturaAceitaDesbloqueiaConflitantes() {
        Mesa mesaA = mesa(eventoId, 9, 13, 5);
        Mesa mesaB = mesa(eventoId, 11, 15, 5);
        Candidatura aceita = candidatura(mesaA);
        aceita.aceitar();
        Candidatura bloqueada = candidatura(mesaB);
        bloqueada.bloquear();
        when(candidaturaRepository.findById(aceita.getId())).thenReturn(Optional.of(aceita));
        when(candidaturaRepository.findByUsuarioIdAndStatus(usuarioId, StatusCandidatura.ACEITA)).thenReturn(List.of());
        when(candidaturaRepository.findByUsuarioIdAndStatus(usuarioId, StatusCandidatura.PENDENTE))
                .thenReturn(List.of(bloqueada));
        salvarDevolveArgumento();

        service.remover(aceita.getId());

        verify(candidaturaRepository).delete(aceita);
        assertThat(bloqueada.isBloqueada()).isFalse();
    }
}
