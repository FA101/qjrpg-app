package com.qjrpg.api.candidatura;

import com.qjrpg.api.mesa.Mesa;
import com.qjrpg.api.mesa.MesaRepository;
import com.qjrpg.api.mesa.StatusMesa;
import com.qjrpg.api.notificacao.NotificacaoService;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Regras de candidatura (PRD secao 13, RF10, RF44-RF47):
 * - so em mesa aprovada, com vaga, uma vez por mesa, e nunca na propria mesa;
 * - candidaturas com horario conflitante (mesmo evento) sao permitidas, mas geram
 *   notificacao ao usuario e a Admins/Moderadores (RF45);
 * - aceitar uma bloqueia as conflitantes pendentes (RF46); bloqueada nao pode ser aceita;
 * - remover uma aceita desbloqueia as conflitantes que nao conflitam com outra aceita (RF47).
 */
@Service
public class CandidaturaServiceImpl implements CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final MesaRepository mesaRepository;
    private final NotificacaoService notificacaoService;

    public CandidaturaServiceImpl(CandidaturaRepository candidaturaRepository, MesaRepository mesaRepository,
                                   NotificacaoService notificacaoService) {
        this.candidaturaRepository = candidaturaRepository;
        this.mesaRepository = mesaRepository;
        this.notificacaoService = notificacaoService;
    }

    @Override
    public Candidatura candidatar(UUID mesaId, UUID usuarioId) {
        Mesa mesa = buscarMesa(mesaId);
        if (mesa.getStatus() != StatusMesa.APROVADA) {
            throw new RegraDeNegocioException("So e possivel se candidatar a mesas aprovadas");
        }
        if (mesa.getGameMasterId().equals(usuarioId)) {
            throw new RegraDeNegocioException("O Game Master nao pode se candidatar a propria mesa");
        }
        if (candidaturaRepository.findByMesaIdAndUsuarioId(mesaId, usuarioId).isPresent()) {
            throw new RegraDeNegocioException("Voce ja se candidatou a esta mesa");
        }
        if (vagasDisponiveis(mesa) <= 0) {
            throw new RegraDeNegocioException("Mesa lotada: nao ha vagas disponiveis");
        }

        Candidatura nova = new Candidatura(mesaId, usuarioId);
        boolean temConflito = false;
        for (Candidatura existente : candidaturaRepository.findByUsuarioId(usuarioId)) {
            if (existente.getStatus() == StatusCandidatura.RECUSADA) continue;
            Mesa outraMesa = buscarMesa(existente.getMesaId());
            if (!conflitam(mesa, outraMesa)) continue;
            temConflito = true;
            if (existente.getStatus() == StatusCandidatura.ACEITA) {
                nova.bloquear(); // ja aceito em mesa conflitante: nasce bloqueada (RF46)
            }
        }
        Candidatura salva = candidaturaRepository.save(nova);

        if (temConflito) { // RF45
            notificacaoService.notificar(usuarioId,
                    "Voce tem candidaturas com horarios conflitantes no mesmo evento.");
            notificacaoService.notificarAdministracao(
                    "Conflito de horario entre candidaturas do usuario " + usuarioId + ".");
        }
        return salva;
    }

    @Override
    public List<Candidatura> listarPorMesa(UUID mesaId) { return candidaturaRepository.findByMesaId(mesaId); }

    @Override
    public Candidatura buscarPorId(UUID id) {
        return candidaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Candidatura nao encontrada: " + id));
    }

    @Override
    public Candidatura aceitar(UUID id) {
        Candidatura candidatura = buscarPorId(id);
        if (candidatura.getStatus() != StatusCandidatura.PENDENTE) {
            throw new RegraDeNegocioException("Somente candidaturas pendentes podem ser aceitas");
        }
        if (candidatura.isBloqueada()) {
            throw new RegraDeNegocioException(
                    "Candidatura bloqueada por conflito de horario com outra candidatura ja aceita");
        }
        Mesa mesaAceita = buscarMesa(candidatura.getMesaId());
        if (vagasDisponiveis(mesaAceita) <= 0) {
            throw new RegraDeNegocioException("Mesa lotada: nao ha vagas disponiveis");
        }
        candidatura.aceitar();
        candidaturaRepository.save(candidatura);

        List<Candidatura> outrasPendentes = candidaturaRepository
                .findByUsuarioIdAndStatus(candidatura.getUsuarioId(), StatusCandidatura.PENDENTE);
        for (Candidatura outra : outrasPendentes) {
            if (outra == candidatura || (outra.getId() != null && outra.getId().equals(candidatura.getId()))) continue;
            Mesa mesaOutra = buscarMesa(outra.getMesaId());
            if (conflitam(mesaAceita, mesaOutra) && !outra.isBloqueada()) {
                outra.bloquear();
                candidaturaRepository.save(outra);
                notificacaoService.notificar(outra.getUsuarioId(),
                        "Uma candidatura sua ficou bloqueada por conflito de horario com outra ja aceita.");
                notificacaoService.notificarAdministracao(
                        "Conflito de horario: usuario " + outra.getUsuarioId() + " teve candidatura bloqueada.");
            }
        }
        notificacaoService.notificar(candidatura.getUsuarioId(), "Sua candidatura foi aceita."); // RF11/RF20
        return candidatura;
    }

    @Override
    public Candidatura recusar(UUID id) {
        Candidatura candidatura = buscarPorId(id);
        if (candidatura.getStatus() != StatusCandidatura.PENDENTE) {
            throw new RegraDeNegocioException("Somente candidaturas pendentes podem ser recusadas");
        }
        candidatura.recusar();
        Candidatura salva = candidaturaRepository.save(candidatura);
        notificacaoService.notificar(candidatura.getUsuarioId(), "Sua candidatura foi recusada."); // RF11/RF20
        return salva;
    }

    @Override
    public void remover(UUID id) {
        Candidatura candidatura = buscarPorId(id);
        boolean estavaAceita = candidatura.getStatus() == StatusCandidatura.ACEITA;
        candidaturaRepository.delete(candidatura);
        if (!estavaAceita) return;

        // RF47: desbloqueia as que nao conflitam com nenhuma outra candidatura ainda aceita
        UUID usuarioId = candidatura.getUsuarioId();
        List<Candidatura> aceitasRestantes = candidaturaRepository
                .findByUsuarioIdAndStatus(usuarioId, StatusCandidatura.ACEITA).stream()
                .filter(c -> !sameId(c, candidatura))
                .toList();
        for (Candidatura bloqueada : candidaturaRepository.findByUsuarioIdAndStatus(usuarioId, StatusCandidatura.PENDENTE)) {
            if (!bloqueada.isBloqueada()) continue;
            Mesa mesaBloqueada = buscarMesa(bloqueada.getMesaId());
            boolean aindaConflita = aceitasRestantes.stream()
                    .anyMatch(aceita -> conflitam(buscarMesa(aceita.getMesaId()), mesaBloqueada));
            if (!aindaConflita) {
                bloqueada.desbloquear();
                candidaturaRepository.save(bloqueada);
            }
        }
    }

    private static boolean sameId(Candidatura a, Candidatura b) {
        return a == b || (a.getId() != null && a.getId().equals(b.getId()));
    }

    /** Conflito so existe entre mesas do mesmo evento com horarios sobrepostos (horario encostado nao conflita). */
    private static boolean conflitam(Mesa a, Mesa b) {
        if (a == b) return false;
        if (a.getId() != null && a.getId().equals(b.getId())) return false;
        return a.getEventoId().equals(b.getEventoId()) && a.sobrepoe(b.getHoraInicio(), b.getHoraFim());
    }

    private int vagasDisponiveis(Mesa mesa) {
        long aceitas = candidaturaRepository.countByMesaIdAndStatus(mesa.getId(), StatusCandidatura.ACEITA);
        return mesa.getVagasTotais() - mesa.getVagasReservadas() - (int) aceitas;
    }

    private Mesa buscarMesa(UUID id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mesa nao encontrada: " + id));
    }
}
