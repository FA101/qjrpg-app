package com.qjrpg.api.link;
import com.qjrpg.api.link.dto.LinkUtilRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class LinkUtilServiceImpl implements LinkUtilService {
    private final LinkUtilRepository repository;
    public LinkUtilServiceImpl(LinkUtilRepository repository) { this.repository = repository; }

    @Override
    public LinkUtil criar(LinkUtilRequest r) { return repository.save(new LinkUtil(r.titulo(), r.url(), r.categoria())); }

    @Override
    public List<LinkUtil> listarTodos() { return repository.findAll(); }

    @Override
    public LinkUtil atualizar(UUID id, LinkUtilRequest r) {
        LinkUtil l = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Link nao encontrado: " + id));
        l.atualizar(r.titulo(), r.url(), r.categoria());
        return repository.save(l);
    }

    @Override
    public void excluir(UUID id) {
        repository.delete(repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Link nao encontrado: " + id)));
    }
}
