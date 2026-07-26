package com.qjrpg.api.candidatura;

import com.qjrpg.api.candidatura.dto.CandidaturaRequest;
import com.qjrpg.api.mesa.Mesa;
import com.qjrpg.api.mesa.MesaRepository;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementa RF44-RF47: candidatar em varias mesas, detectar conflito de horario,
 * bloquear conflitantes ao aceitar, e desbloquear ao remover.
 * Nota: notificacao (RF45) fica marcada com comentario - entra quando o modulo
 * de Notificacao/FCM for implementado na fase de autenticacao.
 */
@Service
public class CandidaturaServiceImpl implements CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final MesaRepository mesaRepository;

    public CandidaturaServiceImpl(CandidaturaRepository candidaturaRepository, MesaRepository mesaRepository) {
        this.candidaturaRepository = candidaturaRepository;
        this.mesaRepository = mesaRepository;
    }

    @Override
    public Candidatura candidatar(CandidaturaRequest r) {
        return candidaturaRepository.save(new Candidatura(r.mesaId(), r.usuarioId()));
    }

    @Override
    public List<Candidatura> listarPorMesa(UUID mesaId) { return candidaturaRepository.findByMesaId(mesaId); }

    @Override
    public Candidatura aceitar(UUID id) {
        Candidatura candidatura = buscarPorId(id);
        Mesa mesaAceita = buscarMesa(candidatura.getMesaId());
        candidatura.aceitar();
        candidaturaRepository.save(candidatura);

        List<Candidatura> outrasPendentes = candidaturaRepository
                .findByUsuarioIdAndStatus(candidatura.getUsuarioId(), StatusCandidatura.PENDENTE);

        for (Candidatura outra : outrasPendentes) {
            if (outra.getId() != null && outra.getId().equals(candidatura.getId())) continue;
            Mesa mesaOutra = buscarMesa(outra.getMesaId());
            if (mesaAceita.sobrepoe(mesaOutra.getHoraInicio(), mesaOutra.getHoraFim())) {
                outra.bloquear();
                candidaturaRepository.save(outra);
                // TODO (fase de notificacao): notificar usuario, admins e moderadores (RF45)
            }
        }
        return candidatura;
    }

    @Override
    public Candidatura recusar(UUID id) {
        Candidatura candidatura = buscarPorId(id);
        candidatura.recusar();
        return candidaturaRepository.save(candidatura);
    }

    @Override
    public void remover(UUID id) {
        Candidatura candidatura = buscarPorId(id);
        boolean estavaAceita = candidatura.getStatus() == StatusCandidatura.ACEITA;
        Mesa mesaRemovida = estavaAceita ? buscarMesa(candidatura.getMesaId()) : null;
        candidaturaRepository.delete(candidatura);

        // RF47: remover uma candidatura aceita desbloqueia as conflitantes do mesmo usuario
        if (estavaAceita) {
            List<Candidatura> bloqueadas = candidaturaRepository
                    .findByUsuarioIdAndStatus(candidatura.getUsuarioId(), StatusCandidatura.PENDENTE);
            for (Candidatura outra : bloqueadas) {
                if (!outra.isBloqueada()) continue;
                Mesa mesaOutra = buscarMesa(outra.getMesaId());
                if (mesaRemovida.sobrepoe(mesaOutra.getHoraInicio(), mesaOutra.getHoraFim())) {
                    outra.desbloquear();
                    candidaturaRepository.save(outra);
                }
            }
        }
    }

    private Candidatura buscarPorId(UUID id) {
        return candidaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Candidatura nao encontrada: " + id));
    }

    private Mesa buscarMesa(UUID id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mesa nao encontrada: " + id));
    }
}
