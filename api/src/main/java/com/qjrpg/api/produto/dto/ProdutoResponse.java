package com.qjrpg.api.produto.dto;
import com.qjrpg.api.produto.Produto;
import com.qjrpg.api.produto.TipoProduto;
import java.util.UUID;

public record ProdutoResponse(UUID id, UUID usuarioId, UUID eventoId, TipoProduto tipo, String titulo,
                               String descricao, String imagemUrl, String linkExterno) {
    public static ProdutoResponse de(Produto p) {
        return new ProdutoResponse(p.getId(), p.getUsuarioId(), p.getEventoId(), p.getTipo(),
                p.getTitulo(), p.getDescricao(), p.getImagemUrl(), p.getLinkExterno());
    }
}
