package com.qjrpg.api.produto;
import com.qjrpg.api.produto.dto.ProdutoRequest;
import java.util.List;
import java.util.UUID;

public interface ProdutoService {
    Produto criar(ProdutoRequest request);
    List<Produto> listarPorEvento(UUID eventoId);
    Produto buscarPorId(UUID id);
    Produto atualizar(UUID id, ProdutoRequest request);
    void excluir(UUID id);
}
