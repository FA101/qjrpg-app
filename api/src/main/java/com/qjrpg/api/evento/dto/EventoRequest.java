package com.qjrpg.api.evento.dto;

import com.qjrpg.api.evento.StatusEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventoRequest(
        @NotBlank(message = "nome e obrigatorio") String nome,
        @NotBlank(message = "local e obrigatorio") String local,
        String linkMapa,
        @NotNull(message = "status e obrigatorio") StatusEvento status
) {
}
