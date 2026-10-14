package com.qjrpg.api.usuario;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false, unique = true) private String email;
    private String nome;
    private String celular;
    @Column(unique = true) private String apelido;
    @Column(nullable = false) private boolean mostrarNomeReal;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PapelUsuario papel;
    @Column(nullable = false) private boolean ativo;
    private String codigoVerificacao;
    private Instant codigoExpiraEm;
    @Column(nullable = false) private Instant criadoEm;

    private LocalDate dataNascimento;
    private String sexo; // "M" ou "F", autodeclarado e opcional (ver PRD)
    private Instant suspensoAte;
    @Column(nullable = false, columnDefinition = "boolean default true") private boolean notificacoesPushAtivas;

    // RF30-31: 2FA por app autenticador (TOTP)
    private String totpSecret;
    @Column(nullable = false, columnDefinition = "boolean default false") private boolean totpAtivo;

    // Desafio de 2FA: emitido apos o codigo de e-mail correto; o segundo fator so e
    // aceito junto com ele (impede pular o primeiro fator conhecendo apenas o e-mail).
    private String desafio2fa;
    private Instant desafio2faExpiraEm;

    // Limite de tentativas para codigo de e-mail e codigo TOTP (anti forca bruta).
    @Column(nullable = false, columnDefinition = "integer default 0") private int tentativasFalhas;

    // Menor de idade: vinculo com responsavel (RF34-36)
    private String responsavelEmail;
    @Column(nullable = false, columnDefinition = "boolean default true") private boolean vinculoResponsavelConfirmado;

    protected Usuario() {}

    public Usuario(String email, PapelUsuario papel) {
        this.email = email;
        this.papel = papel;
        this.ativo = true;
        this.mostrarNomeReal = false;
        this.notificacoesPushAtivas = true;
        this.vinculoResponsavelConfirmado = true; // so vira false quando ha responsavel pendente
        this.criadoEm = Instant.now();
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getNome() { return nome; }
    public String getCelular() { return celular; }
    public String getApelido() { return apelido; }
    public boolean isMostrarNomeReal() { return mostrarNomeReal; }
    public PapelUsuario getPapel() { return papel; }
    public boolean isAtivo() { return ativo; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getSexo() { return sexo; }
    public boolean isNotificacoesPushAtivas() { return notificacoesPushAtivas; }
    public String getResponsavelEmail() { return responsavelEmail; }
    public boolean isVinculoResponsavelConfirmado() { return vinculoResponsavelConfirmado; }

    public void definirCodigo(String codigo, Instant expiraEm) {
        this.codigoVerificacao = codigo;
        this.codigoExpiraEm = expiraEm;
        this.tentativasFalhas = 0;
    }

    public void limparCodigo() {
        this.codigoVerificacao = null;
        this.codigoExpiraEm = null;
        this.tentativasFalhas = 0;
    }

    public static final int MAX_TENTATIVAS = 5;

    /** Registra uma tentativa falha; ao atingir o limite, invalida codigo e desafio pendentes. */
    public void registrarTentativaFalha() {
        this.tentativasFalhas++;
        if (tentativasFalhas >= MAX_TENTATIVAS) {
            this.codigoVerificacao = null;
            this.codigoExpiraEm = null;
            this.desafio2fa = null;
            this.desafio2faExpiraEm = null;
        }
    }

    public int getTentativasFalhas() { return tentativasFalhas; }

    public void definirDesafio2fa(String desafio, Instant expiraEm) {
        this.desafio2fa = desafio;
        this.desafio2faExpiraEm = expiraEm;
    }

    public boolean desafio2faValido(String desafio) {
        return desafio2fa != null && desafio != null && desafio2fa.equals(desafio)
                && desafio2faExpiraEm != null && desafio2faExpiraEm.isAfter(Instant.now());
    }

    public void limparDesafio2fa() {
        this.desafio2fa = null;
        this.desafio2faExpiraEm = null;
        this.tentativasFalhas = 0;
    }

    public boolean codigoValido(String codigo) {
        return codigoVerificacao != null && codigoVerificacao.equals(codigo)
                && codigoExpiraEm != null && codigoExpiraEm.isAfter(Instant.now());
    }

    public void completarCadastro(String nome, String celular, String apelido,
                                   LocalDate dataNascimento, String sexo, String responsavelEmail) {
        if (nome != null && !nome.isBlank()) this.nome = nome;
        if (celular != null && !celular.isBlank()) this.celular = celular;
        if (apelido != null && !apelido.isBlank()) this.apelido = apelido;
        if (sexo != null && !sexo.isBlank()) this.sexo = sexo;
        if (dataNascimento != null) {
            this.dataNascimento = dataNascimento;
            if (isMenorDeIdade() && responsavelEmail != null && !responsavelEmail.isBlank()) {
                this.responsavelEmail = responsavelEmail;
                this.vinculoResponsavelConfirmado = false;
            }
        }
    }

    public boolean isMenorDeIdade() {
        if (dataNascimento == null) return false;
        return Period.between(dataNascimento, LocalDate.now()).getYears() < 18;
    }

    public void confirmarVinculoResponsavel() { this.vinculoResponsavelConfirmado = true; }
    public void revogarVinculoResponsavel() {
        this.responsavelEmail = null;
        this.vinculoResponsavelConfirmado = true;
    }

    public void definirApelido(String apelido) { this.apelido = apelido; }
    public void limparApelido() { this.apelido = null; }
    public void definirPreferenciaNome(boolean mostrarNomeReal) { this.mostrarNomeReal = mostrarNomeReal; }
    public void definirPapel(PapelUsuario papel) { this.papel = papel; }
    public void definirAtivo(boolean ativo) { this.ativo = ativo; }
    public void definirNotificacoesPushAtivas(boolean ativas) { this.notificacoesPushAtivas = ativas; }

    public String getTotpSecret() { return totpSecret; }
    public boolean isTotpAtivo() { return totpAtivo; }
    public void definirTotpSecretPendente(String segredo) { this.totpSecret = segredo; this.totpAtivo = false; }
    public void ativarTotp() { this.totpAtivo = true; }
    public void desativarTotp() { this.totpSecret = null; this.totpAtivo = false; }

    // RF38: suspensao temporaria (ate 90 dias), reversivel pelo proprio usuario
    public void suspenderPor(int dias) {
        int diasLimitados = Math.min(dias, 90);
        this.suspensoAte = Instant.now().plusSeconds((long) diasLimitados * 24 * 60 * 60);
    }

    public void reativar() { this.suspensoAte = null; }

    public boolean estaSuspenso() {
        return suspensoAte != null && suspensoAte.isAfter(Instant.now());
    }

    // RF39: exclusao (anonimizacao) - nao apaga a linha para nao quebrar referencias
    // (mesas/mensagens/etc guardam so o UUID, sem FK de banco), mas remove todo dado pessoal.
    public void excluirDadosPessoais() {
        this.nome = null;
        this.celular = null;
        this.apelido = null;
        this.dataNascimento = null;
        this.sexo = null;
        this.responsavelEmail = null;
        this.totpSecret = null;
        this.totpAtivo = false;
        this.ativo = false;
        this.email = "excluido-" + this.id + "@qjrpg.local";
    }

    public String nomeExibicao() {
        if (mostrarNomeReal && nome != null && !nome.isBlank()) return nome;
        return apelido != null ? apelido : "Usuario";
    }
}
