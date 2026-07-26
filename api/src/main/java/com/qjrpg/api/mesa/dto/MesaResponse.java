package com.qjrpg.api.mesa.dto;
import com.qjrpg.api.mesa.Mesa;
import com.qjrpg.api.mesa.StatusMesa;
import java.time.LocalTime;
import java.util.UUID;
public record MesaResponse(UUID id, UUID eventoId, UUID gameMasterId, String tipoJogo,
        LocalTime horaInicio, LocalTime horaFim, int vagas, StatusMesa status) {
    public static MesaResponse de(Mesa m) {
        return new MesaResponse(m.getId(), m.getEventoId(), m.getGameMasterId(), m.getTipoJogo(),
                m.getHoraInicio(), m.getHoraFim(), m.getVagas(), m.getStatus());
    }
}
