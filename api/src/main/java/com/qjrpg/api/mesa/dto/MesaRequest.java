package com.qjrpg.api.mesa.dto;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.UUID;

// gameMasterId e ignorado pela API (compatibilidade): o Game Master e sempre o usuario autenticado.
public record MesaRequest(
        @NotNull UUID eventoId, UUID gameMasterId, Integer numero,
        @NotBlank String tipoJogo, String sistemaJogo, String tituloAventura, String sinopse,
        String palavrasChave, String observacoes, String faixaEtaria,
        @NotNull LocalTime horaInicio, @NotNull LocalTime horaFim,
        @Min(1) int vagasTotais, @Min(0) int vagasReservadas) {}
