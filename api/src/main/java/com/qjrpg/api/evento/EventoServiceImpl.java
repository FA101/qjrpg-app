package com.qjrpg.api.evento;

import com.qjrpg.api.evento.dto.EventoRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoRepository repository;

    public EventoServiceImpl(EventoRepository repository) { this.repository = repository; }

    @Override
    public Evento criar(EventoRequest r) {
        return repository.save(new Evento(r.nome(), r.data(), r.local(), r.linkMapa(), r.status(),
                r.horaInicioJanela(), r.horaFimJanela()));
    }

    @Override
    public List<Evento> listarTodos() { return repository.findAll(); }

    @Override
    public Evento buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento nao encontrado: " + id));
    }

    @Override
    public Evento atualizar(UUID id, EventoRequest r) {
        Evento evento = buscarPorId(id);
        evento.atualizar(r.nome(), r.data(), r.local(), r.linkMapa(), r.status(), r.horaInicioJanela(), r.horaFimJanela());
        return repository.save(evento);
    }

    @Override
    public void excluir(UUID id) { repository.delete(buscarPorId(id)); }
}
