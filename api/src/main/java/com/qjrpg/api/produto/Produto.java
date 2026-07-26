package com.qjrpg.api.produto;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private UUID usuarioId;
    @Column(nullable = false) private UUID eventoId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private TipoProduto tipo;
    @Column(nullable = false) private String titulo;
    private String descricao;
    private String imagemUrl;
    private String linkExterno;

    protected Produto() {}

    public Produto(UUID usuarioId, UUID eventoId, TipoProduto tipo, String titulo, String descricao,
                    String imagemUrl, String linkExterno) {
        this.usuarioId = usuarioId; this.eventoId = eventoId; this.tipo = tipo; this.titulo = titulo;
        this.descricao = descricao; this.imagemUrl = imagemUrl; this.linkExterno = linkExterno;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getEventoId() { return eventoId; }
    public TipoProduto getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getImagemUrl() { return imagemUrl; }
    public String getLinkExterno() { return linkExterno; }

    public void atualizar(TipoProduto tipo, String titulo, String descricao, String imagemUrl, String linkExterno) {
        this.tipo = tipo; this.titulo = titulo; this.descricao = descricao;
        this.imagemUrl = imagemUrl; this.linkExterno = linkExterno;
    }
}
