package com.qjrpg.api.produto;
import com.qjrpg.api.produto.dto.ProdutoRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.tag.TagService;
import com.qjrpg.api.tag.TagSistema;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ProdutoServiceImpl implements ProdutoService {
    private final ProdutoRepository repository;
    private final TagService tagService;

    public ProdutoServiceImpl(ProdutoRepository repository, TagService tagService) {
        this.repository = repository;
        this.tagService = tagService;
    }

    @Override
    public Produto criar(ProdutoRequest r, UUID usuarioId) {
        Produto salvo = repository.save(new Produto(usuarioId, r.eventoId(), r.tipo(), r.titulo(),
                r.descricao(), r.imagemUrl(), r.linkExterno()));
        tagService.atribuirTagDoSistema(usuarioId, TagSistema.VENDAS); // RF15
        return salvo;
    }

    @Override
    public List<Produto> listarPorEvento(UUID eventoId) { return repository.findByEventoId(eventoId); }

    @Override
    public Produto buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado: " + id));
    }

    @Override
    public Produto atualizar(UUID id, ProdutoRequest r) {
        Produto p = buscarPorId(id);
        p.atualizar(r.tipo(), r.titulo(), r.descricao(), r.imagemUrl(), r.linkExterno());
        return repository.save(p);
    }

    @Override
    public void excluir(UUID id) { repository.delete(buscarPorId(id)); }
}
