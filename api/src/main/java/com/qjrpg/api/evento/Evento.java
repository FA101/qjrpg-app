package com.qjrpg.api.evento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalTime;
import java.util.UUID;

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

    // Janela de horario do evento (simplificacao v1: um dia por evento;
    // multi-dia com DiaDeEvento fica para uma proxima iteracao).
    private LocalTime horaInicioJanela;
    private LocalTime horaFimJanela;

    protected Evento() {
    }

    public Evento(String nome, String local, String linkMapa, StatusEvento status,
                  LocalTime horaInicioJanela, LocalTime horaFimJanela) {
        this.nome = nome;
        this.local = local;
        this.linkMapa = linkMapa;
        this.status = status;
        this.horaInicioJanela = horaInicioJanela;
        this.horaFimJanela = horaFimJanela;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getLocal() { return local; }
    public String getLinkMapa() { return linkMapa; }
    public StatusEvento getStatus() { return status; }
    public LocalTime getHoraInicioJanela() { return horaInicioJanela; }
    public LocalTime getHoraFimJanela() { return horaFimJanela; }

    public void atualizar(String nome, String local, String linkMapa, StatusEvento status,
                           LocalTime horaInicioJanela, LocalTime horaFimJanela) {
        this.nome = nome;
        this.local = local;
        this.linkMapa = linkMapa;
        this.status = status;
        this.horaInicioJanela = horaInicioJanela;
        this.horaFimJanela = horaFimJanela;
    }

    public boolean dentroDaJanela(LocalTime inicio, LocalTime fim) {
        if (horaInicioJanela == null || horaFimJanela == null) {
            return true; // evento sem janela definida ainda: nao bloqueia
        }
        return !inicio.isBefore(horaInicioJanela) && !fim.isAfter(horaFimJanela);
    }
}
