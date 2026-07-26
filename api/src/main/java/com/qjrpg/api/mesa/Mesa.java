package com.qjrpg.api.mesa;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "mesas")
public class Mesa {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private UUID eventoId;
    @Column(nullable = false) private UUID gameMasterId;
    @Column(nullable = false) private String tipoJogo;
    @Column(nullable = false) private LocalTime horaInicio;
    @Column(nullable = false) private LocalTime horaFim;
    @Column(nullable = false) private int vagas;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusMesa status;

    protected Mesa() {}

    public Mesa(UUID eventoId, UUID gameMasterId, String tipoJogo, LocalTime horaInicio, LocalTime horaFim, int vagas) {
        this.eventoId = eventoId; this.gameMasterId = gameMasterId; this.tipoJogo = tipoJogo;
        this.horaInicio = horaInicio; this.horaFim = horaFim; this.vagas = vagas;
        this.status = StatusMesa.PENDENTE;
    }

    public UUID getId() { return id; }
    public UUID getEventoId() { return eventoId; }
    public UUID getGameMasterId() { return gameMasterId; }
    public String getTipoJogo() { return tipoJogo; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public int getVagas() { return vagas; }
    public StatusMesa getStatus() { return status; }

    public void atualizarStatus(StatusMesa novoStatus) { this.status = novoStatus; }

    // RF48: duas mesas se sobrepoem se um intervalo comeca antes do outro terminar.
    // Horarios encostados (14h-18h e 18h-22h) NAO sao sobreposicao (RF: "podem ter
    // horario de inicio igual ao de termino da anterior").
    public boolean sobrepoe(LocalTime outraInicio, LocalTime outraFim) {
        return this.horaInicio.isBefore(outraFim) && outraInicio.isBefore(this.horaFim);
    }
}
