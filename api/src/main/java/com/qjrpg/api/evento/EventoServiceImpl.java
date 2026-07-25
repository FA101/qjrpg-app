package com.qjrpg.api.evento;

import com.qjrpg.api.evento.dto.EventoRequest;
import com.qjrpg.api.shared.exception.EventoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoRepository repository;

    // injecao via construtor: facilita passar um mock nos testes
    public EventoServiceImpl(EventoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Evento criar(EventoRequest request) {
        Evento evento = new Evento(request.nome(), request.local(), request.linkMapa(), request.status());
        return repository.save(evento);
    }

    @Override
    public List<Evento> listarTodos() {
        return repository.findAll();
    }

    @Override
    public Evento buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EventoNaoEncontradoException(id));
    }

    @Override
    public Evento atualizar(UUID id, EventoRequest request) {
        Evento evento = buscarPorId(id);
        evento.atualizar(request.nome(), request.local(), request.linkMapa(), request.status());
        return repository.save(evento);
    }

    @Override
    public void excluir(UUID id) {
        Evento evento = buscarPorId(id);
        repository.delete(evento);
    }
}
