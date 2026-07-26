package com.qjrpg.api.candidatura.dto;
import com.qjrpg.api.candidatura.Candidatura;
import com.qjrpg.api.candidatura.StatusCandidatura;
import java.util.UUID;
public record CandidaturaResponse(UUID id, UUID mesaId, UUID usuarioId, StatusCandidatura status, boolean bloqueada) {
    public static CandidaturaResponse de(Candidatura c) {
        return new CandidaturaResponse(c.getId(), c.getMesaId(), c.getUsuarioId(), c.getStatus(), c.isBloqueada());
    }
}
