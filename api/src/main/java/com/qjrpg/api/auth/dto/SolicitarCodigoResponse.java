package com.qjrpg.api.auth.dto;

// codigoParaTeste so e preenchido quando app.auth.expor-codigo=true (desenvolvimento,
// enquanto nao ha envio real de e-mail). Em producao deve ser false.
public record SolicitarCodigoResponse(String mensagem, String codigoParaTeste) {}
