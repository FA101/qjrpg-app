package com.qjrpg.api.evento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entidade de dominio. Nao expoe setters soltos: qualquer alteracao passa
 * pelo metodo atualizar(), que concentra a regra em um unico lugar
 * (Single Responsibility / evita estado inconsistente espalhado pelo codigo).
 */
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String local;

    private String linkMapa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status;

    protected Evento() {
        // construtor exigido pelo JPA, nao usar diretamente
    }

    public Evento(String nome, String local, String linkMapa, StatusEvento status) {
        this.nome = nome;
        this.local = local;
        this.linkMapa = linkMapa;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLocal() {
        return local;
    }

    public String getLinkMapa() {
        return linkMapa;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public void atualizar(String nome, String local, String linkMapa, StatusEvento status) {
        this.nome = nome;
        this.local = local;
        this.linkMapa = linkMapa;
        this.status = status;
    }
}
