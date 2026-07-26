package com.qjrpg.api.produto.dto;
import com.qjrpg.api.produto.TipoProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ProdutoRequest(
        @NotNull UUID usuarioId, @NotNull UUID eventoId, @NotNull TipoProduto tipo,
        @NotBlank String titulo, String descricao, String imagemUrl, String linkExterno) {}
