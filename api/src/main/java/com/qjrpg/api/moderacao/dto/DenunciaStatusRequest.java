package com.qjrpg.api.moderacao.dto;
import com.qjrpg.api.moderacao.StatusDenuncia;
import jakarta.validation.constraints.NotNull;
public record DenunciaStatusRequest(@NotNull StatusDenuncia status) {}
