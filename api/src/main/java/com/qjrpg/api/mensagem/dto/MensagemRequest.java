package com.qjrpg.api.mensagem.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record MensagemRequest(@NotNull UUID mesaId, @NotNull UUID autorId, UUID respostaDeId,
        @NotBlank String conteudo) {}
