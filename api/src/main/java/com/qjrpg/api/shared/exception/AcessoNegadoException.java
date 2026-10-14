package com.qjrpg.api.shared.exception;

/** Usuario autenticado, mas sem permissao para a acao (RF02) - vira HTTP 403. */
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) { super(mensagem); }
}
