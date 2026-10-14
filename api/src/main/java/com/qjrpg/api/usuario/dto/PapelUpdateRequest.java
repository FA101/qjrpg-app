package com.qjrpg.api.usuario.dto;
import com.qjrpg.api.usuario.PapelUsuario;
import jakarta.validation.constraints.NotNull;
public record PapelUpdateRequest(@NotNull PapelUsuario papel) {}
