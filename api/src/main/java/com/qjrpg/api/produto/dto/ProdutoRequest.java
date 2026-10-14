package com.qjrpg.api.produto.dto;
import com.qjrpg.api.produto.TipoProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

// usuarioId e ignorado pela API (compatibilidade): o dono e sempre o usuario autenticado.
public record ProdutoRequest(
        UUID usuarioId, @NotNull UUID eventoId, @NotNull TipoProduto tipo,
        @NotBlank String titulo, String descricao, String imagemUrl, String linkExterno) {}
