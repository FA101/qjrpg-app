package com.qjrpg.api.moderacao;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "denuncias")
public class Denuncia {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private UUID usuarioDenunciadoId;
    @Column(nullable = false) private UUID usuarioDenuncianteId;
    @Column(nullable = false) private String motivo;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusDenuncia status;
    @Column(nullable = false) private Instant criadoEm;

    protected Denuncia() {}

    public Denuncia(UUID usuarioDenunciadoId, UUID usuarioDenuncianteId, String motivo) {
        this.usuarioDenunciadoId = usuarioDenunciadoId;
        this.usuarioDenuncianteId = usuarioDenuncianteId;
        this.motivo = motivo;
        this.status = StatusDenuncia.PENDENTE;
        this.criadoEm = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUsuarioDenunciadoId() { return usuarioDenunciadoId; }
    public UUID getUsuarioDenuncianteId() { return usuarioDenuncianteId; }
    public String getMotivo() { return motivo; }
    public StatusDenuncia getStatus() { return status; }
    public Instant getCriadoEm() { return criadoEm; }

    public void atualizarStatus(StatusDenuncia status) { this.status = status; }
}
