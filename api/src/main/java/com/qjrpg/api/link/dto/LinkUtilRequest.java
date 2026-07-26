package com.qjrpg.api.link.dto;
import jakarta.validation.constraints.NotBlank;
public record LinkUtilRequest(@NotBlank String titulo, @NotBlank String url, @NotBlank String categoria) {}
