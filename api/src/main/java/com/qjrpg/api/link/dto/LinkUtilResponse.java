package com.qjrpg.api.link.dto;
import com.qjrpg.api.link.LinkUtil;
import java.util.UUID;
public record LinkUtilResponse(UUID id, String titulo, String url, String categoria) {
    public static LinkUtilResponse de(LinkUtil l) {
        return new LinkUtilResponse(l.getId(), l.getTitulo(), l.getUrl(), l.getCategoria());
    }
}
