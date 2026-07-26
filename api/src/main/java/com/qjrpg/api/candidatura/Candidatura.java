package com.qjrpg.api.candidatura;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "candidaturas")
public class Candidatura {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private UUID mesaId;
    @Column(nullable = false) private UUID usuarioId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusCandidatura status;
    @Column(nullable = false) private boolean bloqueada;

    protected Candidatura() {}

    public Candidatura(UUID mesaId, UUID usuarioId) {
        this.mesaId = mesaId; this.usuarioId = usuarioId;
        this.status = StatusCandidatura.PENDENTE; this.bloqueada = false;
    }

    public UUID getId() { return id; }
    public UUID getMesaId() { return mesaId; }
    public UUID getUsuarioId() { return usuarioId; }
    public StatusCandidatura getStatus() { return status; }
    public boolean isBloqueada() { return bloqueada; }

    public void aceitar() { this.status = StatusCandidatura.ACEITA; }
    public void recusar() { this.status = StatusCandidatura.RECUSADA; }
    public void bloquear() { this.bloqueada = true; }
    public void desbloquear() { this.bloqueada = false; }
}
