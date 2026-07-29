package com.qjrpg.api.usuario;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false, unique = true) private String email;
    private String nome;
    private String celular;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PapelUsuario papel;
    @Column(nullable = false) private boolean ativo;
    private String codigoVerificacao;
    private Instant codigoExpiraEm;
    @Column(nullable = false) private Instant criadoEm;

    protected Usuario() {}

    public Usuario(String email, PapelUsuario papel) {
        this.email = email;
        this.papel = papel;
        this.ativo = true;
        this.criadoEm = Instant.now();
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getNome() { return nome; }
    public String getCelular() { return celular; }
    public PapelUsuario getPapel() { return papel; }
    public boolean isAtivo() { return ativo; }

    public void definirCodigo(String codigo, Instant expiraEm) {
        this.codigoVerificacao = codigo;
        this.codigoExpiraEm = expiraEm;
    }

    public void limparCodigo() {
        this.codigoVerificacao = null;
        this.codigoExpiraEm = null;
    }

    public boolean codigoValido(String codigo) {
        return codigoVerificacao != null && codigoVerificacao.equals(codigo)
                && codigoExpiraEm != null && codigoExpiraEm.isAfter(Instant.now());
    }

    public void completarCadastro(String nome, String celular) {
        if (nome != null && !nome.isBlank()) this.nome = nome;
        if (celular != null && !celular.isBlank()) this.celular = celular;
    }
}
