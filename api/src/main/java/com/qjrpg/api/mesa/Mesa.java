package com.qjrpg.api.mesa;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "mesas")
public class Mesa {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private UUID eventoId;
    @Column(nullable = false) private UUID gameMasterId;
    private Integer numero;
    @Column(nullable = false) private String tipoJogo;       // RPG, BG, CG, WG
    private String sistemaJogo;                                // "D&D 2024", "Daggerheart"...
    private String tituloAventura;                              // "Campanha na Montanha Subterranea"
    @Column(length = 4000) private String sinopse;              // "Contexto" do exemplo real
    private String palavrasChave;                               // separadas por virgula
    @Column(length = 2000) private String observacoes;          // "Extras"
    private String faixaEtaria;                                 // "Livre", "12+", etc.
    @Column(nullable = false) private LocalTime horaInicio;
    @Column(nullable = false) private LocalTime horaFim;
    @Column(nullable = false) private int vagasTotais;
    @Column(nullable = false) private int vagasReservadas;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusMesa status;

    protected Mesa() {}

    public Mesa(UUID eventoId, UUID gameMasterId, Integer numero, String tipoJogo, String sistemaJogo,
                String tituloAventura, String sinopse, String palavrasChave, String observacoes, String faixaEtaria,
                LocalTime horaInicio, LocalTime horaFim, int vagasTotais, int vagasReservadas) {
        this.eventoId = eventoId;
        this.gameMasterId = gameMasterId;
        this.numero = numero;
        this.tipoJogo = tipoJogo;
        this.sistemaJogo = sistemaJogo;
        this.tituloAventura = tituloAventura;
        this.sinopse = sinopse;
        this.palavrasChave = palavrasChave;
        this.observacoes = observacoes;
        this.faixaEtaria = faixaEtaria;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.vagasTotais = vagasTotais;
        this.vagasReservadas = vagasReservadas;
        this.status = StatusMesa.PENDENTE;
    }

    public UUID getId() { return id; }
    public UUID getEventoId() { return eventoId; }
    public UUID getGameMasterId() { return gameMasterId; }
    public Integer getNumero() { return numero; }
    public String getTipoJogo() { return tipoJogo; }
    public String getSistemaJogo() { return sistemaJogo; }
    public String getTituloAventura() { return tituloAventura; }
    public String getSinopse() { return sinopse; }
    public String getPalavrasChave() { return palavrasChave; }
    public String getObservacoes() { return observacoes; }
    public String getFaixaEtaria() { return faixaEtaria; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public int getVagasTotais() { return vagasTotais; }
    public int getVagasReservadas() { return vagasReservadas; }
    public StatusMesa getStatus() { return status; }

    public void atualizarStatus(StatusMesa novoStatus) { this.status = novoStatus; }

    public boolean sobrepoe(LocalTime outraInicio, LocalTime outraFim) {
        return this.horaInicio.isBefore(outraFim) && outraInicio.isBefore(this.horaFim);
    }
}
