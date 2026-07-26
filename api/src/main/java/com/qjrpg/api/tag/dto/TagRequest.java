package com.qjrpg.api.tag.dto;

import com.qjrpg.api.tag.TipoTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagRequest(
        @NotBlank(message = "nome e obrigatorio") String nome,
        @NotBlank(message = "corHex e obrigatorio") String corHex,
        @NotNull(message = "tipo e obrigatorio") TipoTag tipo,
        String regraAplicacao
) {
}
