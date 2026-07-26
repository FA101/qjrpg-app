package com.qjrpg.api.link;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "links_uteis")
public class LinkUtil {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private String titulo;
    @Column(nullable = false) private String url;
    @Column(nullable = false) private String categoria;

    protected LinkUtil() {}

    public LinkUtil(String titulo, String url, String categoria) {
        this.titulo = titulo; this.url = url; this.categoria = categoria;
    }

    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getUrl() { return url; }
    public String getCategoria() { return categoria; }

    public void atualizar(String titulo, String url, String categoria) {
        this.titulo = titulo; this.url = url; this.categoria = categoria;
    }
}
