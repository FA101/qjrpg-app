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

    /** Garante que as tags fixas do sistema existam (idempotente). */
    void garantirTagsDoSistema();
    /** Atribuicao automatica por regra de negocio (idempotente). */
    void atribuirTagDoSistema(UUID usuarioId, TagSistema tagSistema);
    void removerTagDoSistema(UUID usuarioId, TagSistema tagSistema);
    /** Atribuicao manual (RF26); a permissao sobre o usuario-alvo e validada no controller. */
    void atribuirManual(UUID usuarioId, UUID tagId, UUID atribuidaPor);
    void removerManual(UUID usuarioId, UUID tagId);
    List<Tag> tagsDoUsuario(UUID usuarioId);
}
