package com.qjrpg.api.usuario.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
public record SuspenderRequest(@Min(1) @Max(90) int dias) {}
