package com.qjrpg.api.workshop;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "workshops")
public class Workshop {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private UUID usuarioId;
    @Column(nullable = false) private UUID eventoId;
    @Column(nullable = false) private String tema;
    private String descricao;
    private LocalTime horarioDesejado;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusWorkshop status;

    protected Workshop() {}

    public Workshop(UUID usuarioId, UUID eventoId, String tema, String descricao, LocalTime horarioDesejado) {
        this.usuarioId = usuarioId; this.eventoId = eventoId; this.tema = tema;
        this.descricao = descricao; this.horarioDesejado = horarioDesejado; this.status = StatusWorkshop.PENDENTE;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getEventoId() { return eventoId; }
    public String getTema() { return tema; }
    public String getDescricao() { return descricao; }
    public LocalTime getHorarioDesejado() { return horarioDesejado; }
    public StatusWorkshop getStatus() { return status; }

    public void atualizarStatus(StatusWorkshop novoStatus) { this.status = novoStatus; }
}
