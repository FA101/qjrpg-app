package com.qjrpg.api.workshop.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.UUID;
public record WorkshopRequest(@NotNull UUID usuarioId, @NotNull UUID eventoId,
        @NotBlank String tema, String descricao, LocalTime horarioDesejado) {}
