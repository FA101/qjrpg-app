package com.qjrpg.api.link;
import com.qjrpg.api.link.dto.LinkUtilRequest;
import java.util.List;
import java.util.UUID;
public interface LinkUtilService {
    LinkUtil criar(LinkUtilRequest request);
    List<LinkUtil> listarTodos();
    LinkUtil atualizar(UUID id, LinkUtilRequest request);
    void excluir(UUID id);
}
