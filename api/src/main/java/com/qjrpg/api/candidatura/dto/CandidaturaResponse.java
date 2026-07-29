package com.qjrpg.api.candidatura.dto;
import com.qjrpg.api.candidatura.Candidatura;
import com.qjrpg.api.candidatura.StatusCandidatura;
import java.util.UUID;
public record CandidaturaResponse(UUID id, UUID mesaId, UUID usuarioId, String usuarioNome,
                                   StatusCandidatura status, boolean bloqueada) {
    public static CandidaturaResponse de(Candidatura c, String usuarioNome) {
        return new CandidaturaResponse(c.getId(), c.getMesaId(), c.getUsuarioId(), usuarioNome, c.getStatus(), c.isBloqueada());
    }
}
