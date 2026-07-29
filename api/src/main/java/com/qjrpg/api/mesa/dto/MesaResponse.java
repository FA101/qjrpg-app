package com.qjrpg.api.mesa.dto;
import com.qjrpg.api.mesa.Mesa;
import com.qjrpg.api.mesa.StatusMesa;
import java.time.LocalTime;
import java.util.UUID;

public record MesaResponse(
        UUID id, UUID eventoId, UUID gameMasterId, String gameMasterNome, Integer numero,
        String tipoJogo, String sistemaJogo, String tituloAventura, String sinopse,
        String palavrasChave, String observacoes, String faixaEtaria,
        LocalTime horaInicio, LocalTime horaFim,
        int vagasTotais, int vagasReservadas, int vagasDisponiveis, StatusMesa status) {

    public static MesaResponse de(Mesa m, String gameMasterNome, int vagasDisponiveis) {
        return new MesaResponse(m.getId(), m.getEventoId(), m.getGameMasterId(), gameMasterNome, m.getNumero(),
                m.getTipoJogo(), m.getSistemaJogo(), m.getTituloAventura(), m.getSinopse(),
                m.getPalavrasChave(), m.getObservacoes(), m.getFaixaEtaria(),
                m.getHoraInicio(), m.getHoraFim(), m.getVagasTotais(), m.getVagasReservadas(),
                vagasDisponiveis, m.getStatus());
    }
}
