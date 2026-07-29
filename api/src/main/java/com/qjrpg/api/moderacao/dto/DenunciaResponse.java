package com.qjrpg.api.moderacao.dto;
import com.qjrpg.api.moderacao.Denuncia;
import com.qjrpg.api.moderacao.StatusDenuncia;
import java.time.Instant;
import java.util.UUID;
public record DenunciaResponse(UUID id, UUID usuarioDenunciadoId, UUID usuarioDenuncianteId, String motivo,
                                StatusDenuncia status, Instant criadoEm) {
    public static DenunciaResponse de(Denuncia d) {
        return new DenunciaResponse(d.getId(), d.getUsuarioDenunciadoId(), d.getUsuarioDenuncianteId(),
                d.getMotivo(), d.getStatus(), d.getCriadoEm());
    }
}
