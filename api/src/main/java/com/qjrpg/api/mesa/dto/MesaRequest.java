package com.qjrpg.api.mesa.dto;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.UUID;
public record MesaRequest(
        @NotNull UUID eventoId, @NotNull UUID gameMasterId, @NotBlank String tipoJogo,
        @NotNull LocalTime horaInicio, @NotNull LocalTime horaFim, @Min(1) int vagas) {}
