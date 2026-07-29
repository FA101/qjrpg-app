package com.qjrpg.api.evento.dto;

import com.qjrpg.api.evento.StatusEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventoRequest(
        @NotBlank String nome,
        @NotNull LocalDate data,
        @NotBlank String local,
        String linkMapa,
        @NotNull StatusEvento status,
        LocalTime horaInicioJanela,
        LocalTime horaFimJanela
) {}
