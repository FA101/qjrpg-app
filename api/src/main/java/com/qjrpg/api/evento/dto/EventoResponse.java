package com.qjrpg.api.evento.dto;

import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.evento.StatusEvento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record EventoResponse(
        UUID id, String nome, LocalDate data, String local, String linkMapa, StatusEvento status,
        LocalTime horaInicioJanela, LocalTime horaFimJanela
) {
    public static EventoResponse deEvento(Evento evento) {
        return new EventoResponse(evento.getId(), evento.getNome(), evento.getData(), evento.getLocal(),
                evento.getLinkMapa(), evento.getStatus(), evento.getHoraInicioJanela(), evento.getHoraFimJanela());
    }
}
