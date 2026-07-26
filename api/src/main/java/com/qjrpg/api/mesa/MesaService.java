package com.qjrpg.api.mesa;
import com.qjrpg.api.mesa.dto.MesaRequest;
import java.util.List;
import java.util.UUID;
public interface MesaService {
    Mesa ofertar(MesaRequest request);
    List<Mesa> listarPorEvento(UUID eventoId);
    Mesa buscarPorId(UUID id);
    Mesa atualizarStatus(UUID id, StatusMesa status);
    void excluir(UUID id);
}
