package com.qjrpg.api.produto;

import com.qjrpg.api.produto.dto.ProdutoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceImplTest {
    @Mock private ProdutoRepository repository;
    private ProdutoService service;

    @BeforeEach
    void configurar() { service = new ProdutoServiceImpl(repository); }

    @Test
    void deveCriarProduto() {
        when(repository.save(any(Produto.class))).thenAnswer(c -> c.getArgument(0));
        ProdutoRequest r = new ProdutoRequest(UUID.randomUUID(), UUID.randomUUID(), TipoProduto.DIGITAL,
                "Livro de regras", "PDF", null, "https://loja.exemplo.com");
        Produto p = service.criar(r);
        assertThat(p.getTitulo()).isEqualTo("Livro de regras");
    }
}
