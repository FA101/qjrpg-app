package com.qjrpg.api.conteudo.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record ConteudoRequest(@NotBlank String secao, @NotBlank String titulo, @NotBlank String corpo,
        @NotNull UUID autorId) {}
