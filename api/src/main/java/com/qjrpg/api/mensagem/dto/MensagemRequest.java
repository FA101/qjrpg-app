package com.qjrpg.api.mensagem.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
// autorId e ignorado pela API (compatibilidade): o autor e sempre o usuario autenticado.
public record MensagemRequest(@NotNull UUID mesaId, UUID autorId, UUID respostaDeId,
        @NotBlank String conteudo) {}
