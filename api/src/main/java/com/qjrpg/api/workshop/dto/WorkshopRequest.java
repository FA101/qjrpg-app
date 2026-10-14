package com.qjrpg.api.workshop.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.UUID;
// usuarioId e ignorado pela API (compatibilidade): o proponente e sempre o usuario autenticado.
public record WorkshopRequest(UUID usuarioId, @NotNull UUID eventoId,
        @NotBlank String tema, String descricao, LocalTime horarioDesejado) {}
