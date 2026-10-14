package com.qjrpg.api.candidatura.dto;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * usuarioId e ignorado pela API (mantido por compatibilidade com clientes antigos):
 * o candidato e sempre o usuario autenticado, para impedir candidatura em nome de outro.
 */
public record CandidaturaRequest(@NotNull UUID mesaId, UUID usuarioId) {}
