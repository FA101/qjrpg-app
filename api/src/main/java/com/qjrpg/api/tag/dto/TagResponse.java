package com.qjrpg.api.tag.dto;

import com.qjrpg.api.tag.Tag;
import com.qjrpg.api.tag.TipoTag;
import java.util.UUID;

public record TagResponse(UUID id, String nome, String corHex, TipoTag tipo, String regraAplicacao) {
    public static TagResponse deTag(Tag tag) {
        return new TagResponse(tag.getId(), tag.getNome(), tag.getCorHex(), tag.getTipo(), tag.getRegraAplicacao());
    }
}
