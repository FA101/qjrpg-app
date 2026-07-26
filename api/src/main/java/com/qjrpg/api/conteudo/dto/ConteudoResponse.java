package com.qjrpg.api.conteudo.dto;
import com.qjrpg.api.conteudo.ConteudoInstitucional;
import java.time.Instant;
import java.util.UUID;
public record ConteudoResponse(UUID id, String secao, String titulo, String corpo, UUID autorId, Instant dataAtualizacao) {
    public static ConteudoResponse de(ConteudoInstitucional c) {
        return new ConteudoResponse(c.getId(), c.getSecao(), c.getTitulo(), c.getCorpo(), c.getAutorId(), c.getDataAtualizacao());
    }
}
