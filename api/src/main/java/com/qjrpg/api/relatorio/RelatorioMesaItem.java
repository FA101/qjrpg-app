package com.qjrpg.api.relatorio;
import com.qjrpg.api.mesa.StatusMesa;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record RelatorioMesaItem(
        UUID mesaId, UUID eventoId, String eventoNome, LocalDate eventoData,
        UUID gameMasterId, String gameMasterNome, String sexoGameMaster,
        String tipoJogo, String sistemaJogo, LocalTime horaInicio, LocalTime horaFim,
        int vagasTotais, String faixaEtaria, StatusMesa status) {}
