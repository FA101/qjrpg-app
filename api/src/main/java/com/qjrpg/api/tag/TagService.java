package com.qjrpg.api.tag;

import com.qjrpg.api.tag.dto.TagRequest;
import java.util.List;
import java.util.UUID;

public interface TagService {
    Tag criar(TagRequest request);
    List<Tag> listarTodos();
    Tag buscarPorId(UUID id);
    Tag atualizar(UUID id, TagRequest request);
    void excluir(UUID id);
}
