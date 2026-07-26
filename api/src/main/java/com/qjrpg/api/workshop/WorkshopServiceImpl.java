package com.qjrpg.api.workshop;
import com.qjrpg.api.workshop.dto.WorkshopRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class WorkshopServiceImpl implements WorkshopService {
    private final WorkshopRepository repository;
    public WorkshopServiceImpl(WorkshopRepository repository) { this.repository = repository; }

    @Override
    public Workshop propor(WorkshopRequest r) {
        return repository.save(new Workshop(r.usuarioId(), r.eventoId(), r.tema(), r.descricao(), r.horarioDesejado()));
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
        return repository.save(w);
    }

    @Override
    public void excluir(UUID id) { repository.delete(buscarPorId(id)); }
}
