package com.qjrpg.api.tag;

import com.qjrpg.api.tag.dto.TagRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository repository;

    public TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag criar(TagRequest request) {
        validarCorExclusiva(request.corHex(), null);
        Tag tag = new Tag(request.nome(), request.corHex(), request.tipo(), request.regraAplicacao());
        return repository.save(tag);
    }

    @Override
    public List<Tag> listarTodos() {
        return repository.findAll();
    }

    @Override
    public Tag buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tag nao encontrada: " + id));
    }

    @Override
    public Tag atualizar(UUID id, TagRequest request) {
        Tag tag = buscarPorId(id);
        validarCorExclusiva(request.corHex(), id);
        tag.atualizar(request.nome(), request.corHex(), request.tipo(), request.regraAplicacao());
        return repository.save(tag);
    }

    @Override
    public void excluir(UUID id) {
        repository.delete(buscarPorId(id));
    }

    // RF28: cada tag tem cor exclusiva
    private void validarCorExclusiva(String corHex, UUID idAtual) {
        repository.findByCorHex(corHex).ifPresent(existente -> {
            boolean mesmoRegistro = idAtual != null && idAtual.equals(existente.getId());
            if (!mesmoRegistro) {
                throw new RegraDeNegocioException("Ja existe uma tag com a cor " + corHex);
            }
        });
    }
}
