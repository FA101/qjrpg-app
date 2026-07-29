package com.qjrpg.api.mesa.dto;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.UUID;

public record MesaRequest(
        @NotNull UUID eventoId, @NotNull UUID gameMasterId, Integer numero,
        @NotBlank String tipoJogo, String sistemaJogo, String tituloAventura, String sinopse,
        String palavrasChave, String observacoes, String faixaEtaria,
        @NotNull LocalTime horaInicio, @NotNull LocalTime horaFim,
        @Min(1) int vagasTotais, @Min(0) int vagasReservadas) {}
