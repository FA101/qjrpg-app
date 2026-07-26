package com.qjrpg.api.conteudo;
import com.qjrpg.api.conteudo.dto.ConteudoRequest;
import java.util.List;
import java.util.UUID;
public interface ConteudoInstitucionalService {
    ConteudoInstitucional criar(ConteudoRequest request);
    List<ConteudoInstitucional> listarTodos();
    ConteudoInstitucional buscarPorSecao(String secao);
    ConteudoInstitucional atualizar(UUID id, ConteudoRequest request);
}
