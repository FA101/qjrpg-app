package com.qjrpg.api.tag;

/**
 * Tags fixas do sistema (PRD secao 5). Sao criadas automaticamente na
 * inicializacao e atribuidas por regra (RF09, RF15, RF19) ou por promocao (Moderador).
 */
public enum TagSistema {
    GAME_MASTER("Game Master", "#6A1B9A"),
    VENDAS("Vendas", "#2E7D32"),
    APOIADOR("Apoiador", "#E65100"),
    MODERADOR("Moderador", "#1565C0");

    private final String nome;
    private final String corPadrao;

    TagSistema(String nome, String corPadrao) {
        this.nome = nome;
        this.corPadrao = corPadrao;
    }

    public String getNome() { return nome; }
    public String getCorPadrao() { return corPadrao; }
}
