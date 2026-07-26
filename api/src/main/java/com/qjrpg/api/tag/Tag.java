package com.qjrpg.api.tag;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String corHex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTag tipo;

    private String regraAplicacao;

    protected Tag() {
    }

    public Tag(String nome, String corHex, TipoTag tipo, String regraAplicacao) {
        this.nome = nome;
        this.corHex = corHex;
        this.tipo = tipo;
        this.regraAplicacao = regraAplicacao;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getCorHex() { return corHex; }
    public TipoTag getTipo() { return tipo; }
    public String getRegraAplicacao() { return regraAplicacao; }

    public void atualizar(String nome, String corHex, TipoTag tipo, String regraAplicacao) {
        this.nome = nome;
        this.corHex = corHex;
        this.tipo = tipo;
        this.regraAplicacao = regraAplicacao;
    }
}
