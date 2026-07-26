package com.qjrpg.api.mesa.dto;
import com.qjrpg.api.mesa.StatusMesa;
import jakarta.validation.constraints.NotNull;
public record MesaStatusRequest(@NotNull StatusMesa status) {}
