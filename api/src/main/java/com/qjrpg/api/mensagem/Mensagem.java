package com.qjrpg.api.mensagem;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mensagens")
public class Mensagem {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false) private UUID mesaId;
    @Column(nullable = false) private UUID autorId;
    private UUID respostaDeId;
    @Column(nullable = false, length = 2000) private String conteudo;
    @Column(nullable = false) private Instant dataHora;

    protected Mensagem() {}

    public Mensagem(UUID mesaId, UUID autorId, UUID respostaDeId, String conteudo) {
        this.mesaId = mesaId; this.autorId = autorId; this.respostaDeId = respostaDeId;
        this.conteudo = conteudo; this.dataHora = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getMesaId() { return mesaId; }
    public UUID getAutorId() { return autorId; }
    public UUID getRespostaDeId() { return respostaDeId; }
    public String getConteudo() { return conteudo; }
    public Instant getDataHora() { return dataHora; }
}
