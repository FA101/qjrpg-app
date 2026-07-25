package com.qjrpg.api.evento.dto;

import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.evento.StatusEvento;

import java.util.UUID;

public record EventoResponse(
        UUID id,
        String nome,
        String local,
        String linkMapa,
        StatusEvento status
) {
    public static EventoResponse deEvento(Evento evento) {
        return new EventoResponse(
                evento.getId(),
                evento.getNome(),
                evento.getLocal(),
                evento.getLinkMapa(),
                evento.getStatus()
        );
    }
}
