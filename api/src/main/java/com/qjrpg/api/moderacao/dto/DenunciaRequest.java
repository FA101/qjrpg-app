package com.qjrpg.api.moderacao.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record DenunciaRequest(@NotNull UUID usuarioDenunciadoId, @NotBlank String motivo) {}
