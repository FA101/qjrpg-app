package com.qjrpg.api.tag;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Atribuicao de uma tag a um usuario (RF21: tags sao cumulativas). */
@Entity
@Table(name = "usuario_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "tag_id"}))
public class UsuarioTag {

    @Id @GeneratedValue private UUID id;
    @Column(name = "usuario_id", nullable = false) private UUID usuarioId;
    @Column(name = "tag_id", nullable = false) private UUID tagId;
    @Column(nullable = false) private boolean automatica;
    private UUID atribuidaPor; // null quando automatica
    @Column(nullable = false) private Instant atribuidaEm;

    protected UsuarioTag() {}

    public UsuarioTag(UUID usuarioId, UUID tagId, boolean automatica, UUID atribuidaPor) {
        this.usuarioId = usuarioId;
        this.tagId = tagId;
        this.automatica = automatica;
        this.atribuidaPor = atribuidaPor;
        this.atribuidaEm = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getTagId() { return tagId; }
    public boolean isAutomatica() { return automatica; }
    public UUID getAtribuidaPor() { return atribuidaPor; }
    public Instant getAtribuidaEm() { return atribuidaEm; }
}
