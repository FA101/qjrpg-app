package com.qjrpg.api.candidatura;
import com.qjrpg.api.candidatura.dto.CandidaturaRequest;
import java.util.List;
import java.util.UUID;
public interface CandidaturaService {
    Candidatura candidatar(CandidaturaRequest request);
    List<Candidatura> listarPorMesa(UUID mesaId);
    Candidatura aceitar(UUID id);
    Candidatura recusar(UUID id);
    void remover(UUID id);
}
