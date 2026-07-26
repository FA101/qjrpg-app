package com.qjrpg.api.candidatura.dto;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CandidaturaRequest(@NotNull UUID mesaId, @NotNull UUID usuarioId) {}
