package com.qjrpg.api.conteudo;
import com.qjrpg.api.conteudo.dto.ConteudoRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ConteudoInstitucionalServiceImpl implements ConteudoInstitucionalService {
    private final ConteudoInstitucionalRepository repository;
    public ConteudoInstitucionalServiceImpl(ConteudoInstitucionalRepository repository) { this.repository = repository; }

    @Override
    public ConteudoInstitucional criar(ConteudoRequest r) {
        return repository.save(new ConteudoInstitucional(r.secao(), r.titulo(), r.corpo(), r.autorId()));
    }

    @Override
    public List<ConteudoInstitucional> listarTodos() { return repository.findAll(); }

    @Override
    public ConteudoInstitucional buscarPorSecao(String secao) {
        return repository.findBySecao(secao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Secao nao encontrada: " + secao));
    }

    @Override
    public ConteudoInstitucional atualizar(UUID id, ConteudoRequest r) {
        ConteudoInstitucional c = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conteudo nao encontrado: " + id));
        c.atualizar(r.titulo(), r.corpo(), r.autorId());
        return repository.save(c);
    }
}
