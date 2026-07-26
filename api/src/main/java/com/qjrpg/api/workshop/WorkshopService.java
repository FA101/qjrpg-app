package com.qjrpg.api.workshop;
import com.qjrpg.api.workshop.dto.WorkshopRequest;
import java.util.List;
import java.util.UUID;
public interface WorkshopService {
    Workshop propor(WorkshopRequest request);
    List<Workshop> listarPorEvento(UUID eventoId);
    Workshop buscarPorId(UUID id);
    Workshop atualizarStatus(UUID id, StatusWorkshop status);
    void excluir(UUID id);
}
