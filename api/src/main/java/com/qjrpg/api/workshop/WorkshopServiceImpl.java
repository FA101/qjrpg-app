package com.qjrpg.api.workshop;
import com.qjrpg.api.notificacao.NotificacaoService;
import com.qjrpg.api.workshop.dto.WorkshopRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.tag.TagService;
import com.qjrpg.api.tag.TagSistema;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class WorkshopServiceImpl implements WorkshopService {
    private final WorkshopRepository repository;
    private final NotificacaoService notificacaoService;
    private final TagService tagService;

    public WorkshopServiceImpl(WorkshopRepository repository, NotificacaoService notificacaoService,
                               TagService tagService) {
        this.repository = repository;
        this.notificacaoService = notificacaoService;
        this.tagService = tagService;
    }

    @Override
    public Workshop propor(WorkshopRequest r, UUID usuarioId) {
        return repository.save(new Workshop(usuarioId, r.eventoId(), r.tema(), r.descricao(), r.horarioDesejado()));
    }

    @Override
    public List<Workshop> listarPorEvento(UUID eventoId) { return repository.findByEventoId(eventoId); }

    @Override
    public Workshop buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Workshop nao encontrado: " + id));
    }

    @Override
    public Workshop atualizarStatus(UUID id, StatusWorkshop status) {
        Workshop w = buscarPorId(id);
        w.atualizarStatus(status);
        Workshop salvo = repository.save(w);
        if (status == StatusWorkshop.APROVADO) {
            tagService.atribuirTagDoSistema(w.getUsuarioId(), TagSistema.APOIADOR); // RF19
        }
        notificacaoService.notificar(w.getUsuarioId(), "Seu workshop '" + w.getTema() + "' foi atualizado para: " + status);
        return salvo;
    }

    @Override
    public void excluir(UUID id) { repository.delete(buscarPorId(id)); }
}
