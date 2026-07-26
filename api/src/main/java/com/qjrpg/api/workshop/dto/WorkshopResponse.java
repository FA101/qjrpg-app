package com.qjrpg.api.workshop.dto;
import com.qjrpg.api.workshop.StatusWorkshop;
import com.qjrpg.api.workshop.Workshop;
import java.time.LocalTime;
import java.util.UUID;
public record WorkshopResponse(UUID id, UUID usuarioId, UUID eventoId, String tema, String descricao,
                                LocalTime horarioDesejado, StatusWorkshop status) {
    public static WorkshopResponse de(Workshop w) {
        return new WorkshopResponse(w.getId(), w.getUsuarioId(), w.getEventoId(), w.getTema(),
                w.getDescricao(), w.getHorarioDesejado(), w.getStatus());
    }
}
