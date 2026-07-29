package com.qjrpg.api.evento;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "eventos")
public class Evento {

    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private String nome;
    private LocalDate data; // nullable no banco de proposito (ver nota de migracao no LEIA-ME)
    @Column(nullable = false) private String local;
    private String linkMapa;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusEvento status;
    private LocalTime horaInicioJanela;
    private LocalTime horaFimJanela;

    protected Evento() {}

    public Evento(String nome, LocalDate data, String local, String linkMapa, StatusEvento status,
                  LocalTime horaInicioJanela, LocalTime horaFimJanela) {
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.linkMapa = linkMapa;
        this.status = status;
        this.horaInicioJanela = horaInicioJanela;
        this.horaFimJanela = horaFimJanela;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getData() { return data; }
    public String getLocal() { return local; }
    public String getLinkMapa() { return linkMapa; }
    public StatusEvento getStatus() { return status; }
    public LocalTime getHoraInicioJanela() { return horaInicioJanela; }
    public LocalTime getHoraFimJanela() { return horaFimJanela; }

    public void atualizar(String nome, LocalDate data, String local, String linkMapa, StatusEvento status,
                           LocalTime horaInicioJanela, LocalTime horaFimJanela) {
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.linkMapa = linkMapa;
        this.status = status;
        this.horaInicioJanela = horaInicioJanela;
        this.horaFimJanela = horaFimJanela;
    }

    public boolean dentroDaJanela(LocalTime inicio, LocalTime fim) {
        if (horaInicioJanela == null || horaFimJanela == null) return true;
        return !inicio.isBefore(horaInicioJanela) && !fim.isAfter(horaFimJanela);
    }
}
