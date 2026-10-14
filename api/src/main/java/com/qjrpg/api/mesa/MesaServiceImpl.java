package com.qjrpg.api.mesa;

import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.evento.EventoRepository;
import com.qjrpg.api.mesa.dto.MesaRequest;
import com.qjrpg.api.notificacao.NotificacaoService;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.tag.TagService;
import com.qjrpg.api.tag.TagSistema;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final EventoRepository eventoRepository;
    private final NotificacaoService notificacaoService;
    private final TagService tagService;

    public MesaServiceImpl(MesaRepository mesaRepository, EventoRepository eventoRepository,
                            NotificacaoService notificacaoService, TagService tagService) {
        this.mesaRepository = mesaRepository;
        this.eventoRepository = eventoRepository;
        this.notificacaoService = notificacaoService;
        this.tagService = tagService;
    }

    @Override
    public Mesa ofertar(MesaRequest r, UUID gameMasterId) {
        if (!r.horaInicio().isBefore(r.horaFim())) {
            throw new RegraDeNegocioException("O horario de inicio deve ser anterior ao de termino");
        }
        if (r.vagasReservadas() > r.vagasTotais()) {
            throw new RegraDeNegocioException("Vagas reservadas nao podem exceder as vagas totais");
        }
        Evento evento = eventoRepository.findById(r.eventoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado: " + r.eventoId()));

        if (!evento.dentroDaJanela(r.horaInicio(), r.horaFim())) {
            throw new RegraDeNegocioException("Horario fora da janela do evento (" +
                    evento.getHoraInicioJanela() + " - " + evento.getHoraFimJanela() + ")");
        }

        List<Mesa> mesasDoEvento = mesaRepository.findByEventoId(r.eventoId());

        if (r.numero() != null) {
            boolean numeroDuplicado = mesasDoEvento.stream().anyMatch(m -> r.numero().equals(m.getNumero()));
            if (numeroDuplicado) {
                throw new RegraDeNegocioException("Ja existe uma mesa numero " + r.numero() + " neste evento");
            }
        }

        boolean temSobreposicao = mesasDoEvento.stream()
                .filter(m -> m.getGameMasterId().equals(gameMasterId))
                .anyMatch(m -> m.sobrepoe(r.horaInicio(), r.horaFim()));
        if (temSobreposicao) {
            throw new RegraDeNegocioException("Voce ja tem uma mesa nesse horario neste evento");
        }

        return mesaRepository.save(new Mesa(r.eventoId(), gameMasterId, r.numero(), r.tipoJogo(),
                r.sistemaJogo(), r.tituloAventura(), r.sinopse(), r.palavrasChave(), r.observacoes(), r.faixaEtaria(),
                r.horaInicio(), r.horaFim(), r.vagasTotais(), r.vagasReservadas()));
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
        Mesa salva = mesaRepository.save(mesa);
        if (status == StatusMesa.APROVADA) {
            tagService.atribuirTagDoSistema(mesa.getGameMasterId(), TagSistema.GAME_MASTER); // RF09
        }
        notificacaoService.notificar(mesa.getGameMasterId(),
                "Sua mesa '" + mesa.getTipoJogo() + "' foi atualizada para: " + status);
        return salva;
    }

    @Override
    public void excluir(UUID id) { mesaRepository.delete(buscarPorId(id)); }
}
