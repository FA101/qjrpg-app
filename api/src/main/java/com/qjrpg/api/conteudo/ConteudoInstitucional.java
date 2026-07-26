package com.qjrpg.api.conteudo;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conteudos_institucionais")
public class ConteudoInstitucional {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false, unique = true) private String secao;
    @Column(nullable = false) private String titulo;
    @Column(nullable = false, length = 8000) private String corpo;
    @Column(nullable = false) private UUID autorId;
    @Column(nullable = false) private Instant dataAtualizacao;

    protected ConteudoInstitucional() {}

    public ConteudoInstitucional(String secao, String titulo, String corpo, UUID autorId) {
        this.secao = secao; this.titulo = titulo; this.corpo = corpo;
        this.autorId = autorId; this.dataAtualizacao = Instant.now();
    }

    public UUID getId() { return id; }
    public String getSecao() { return secao; }
    public String getTitulo() { return titulo; }
    public String getCorpo() { return corpo; }
    public UUID getAutorId() { return autorId; }
    public Instant getDataAtualizacao() { return dataAtualizacao; }

    public void atualizar(String titulo, String corpo, UUID autorId) {
        this.titulo = titulo; this.corpo = corpo; this.autorId = autorId; this.dataAtualizacao = Instant.now();
    }
}
