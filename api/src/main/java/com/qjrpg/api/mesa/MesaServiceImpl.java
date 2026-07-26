package com.qjrpg.api.mesa;

import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.evento.EventoRepository;
import com.qjrpg.api.mesa.dto.MesaRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final EventoRepository eventoRepository;

    public MesaServiceImpl(MesaRepository mesaRepository, EventoRepository eventoRepository) {
        this.mesaRepository = mesaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public Mesa ofertar(MesaRequest r) {
        Evento evento = eventoRepository.findById(r.eventoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado: " + r.eventoId()));

        // RF49: mesa nao pode ficar fora da janela do evento
        if (!evento.dentroDaJanela(r.horaInicio(), r.horaFim())) {
            throw new RegraDeNegocioException("Horario fora da janela do evento (" +
                    evento.getHoraInicioJanela() + " - " + evento.getHoraFimJanela() + ")");
        }

        // RF48: usuario nao oferta duas mesas no mesmo horario do mesmo evento
        List<Mesa> mesasDoGameMaster = mesaRepository.findByEventoIdAndGameMasterId(r.eventoId(), r.gameMasterId());
        boolean temSobreposicao = mesasDoGameMaster.stream()
                .anyMatch(mesa -> mesa.sobrepoe(r.horaInicio(), r.horaFim()));
        if (temSobreposicao) {
            throw new RegraDeNegocioException("Voce ja tem uma mesa nesse horario neste evento");
        }

        return mesaRepository.save(new Mesa(r.eventoId(), r.gameMasterId(), r.tipoJogo(),
                r.horaInicio(), r.horaFim(), r.vagas()));
    }

    @Override
    public List<Mesa> listarPorEvento(UUID eventoId) { return mesaRepository.findByEventoId(eventoId); }

    @Override
    public Mesa buscarPorId(UUID id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mesa nao encontrada: " + id));
    }

    @Override
    public Mesa atualizarStatus(UUID id, StatusMesa status) {
        Mesa mesa = buscarPorId(id);
        mesa.atualizarStatus(status);
        return mesaRepository.save(mesa);
    }

    @Override
    public void excluir(UUID id) { mesaRepository.delete(buscarPorId(id)); }
}
