package com.qjrpg.api.evento;

import com.qjrpg.api.evento.dto.EventoRequest;

import java.util.List;
import java.util.UUID;

public interface EventoService {
    Evento criar(EventoRequest request);
    List<Evento> listarTodos();
    Evento buscarPorId(UUID id);
    Evento atualizar(UUID id, EventoRequest request);
    void excluir(UUID id);
}
