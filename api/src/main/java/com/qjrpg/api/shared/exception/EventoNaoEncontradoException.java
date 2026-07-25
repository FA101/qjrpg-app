package com.qjrpg.api.shared.exception;

import java.util.UUID;

public class EventoNaoEncontradoException extends RuntimeException {
    public EventoNaoEncontradoException(UUID id) {
        super("Evento nao encontrado: " + id);
    }
}
