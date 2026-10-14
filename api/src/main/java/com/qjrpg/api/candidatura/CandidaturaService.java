package com.qjrpg.api.candidatura;
import java.util.List;
import java.util.UUID;
public interface CandidaturaService {
    Candidatura candidatar(UUID mesaId, UUID usuarioId);
    List<Candidatura> listarPorMesa(UUID mesaId);
    Candidatura buscarPorId(UUID id);
    Candidatura aceitar(UUID id);
    Candidatura recusar(UUID id);
    void remover(UUID id);
}
