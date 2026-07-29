package com.qjrpg.api.moderacao;
import java.util.List;
import java.util.UUID;
public interface DenunciaService {
    Denuncia denunciar(UUID usuarioDenunciadoId, UUID usuarioDenuncianteId, String motivo);
    List<Denuncia> listarPendentes();
    Denuncia atualizarStatus(UUID id, StatusDenuncia status);
}
