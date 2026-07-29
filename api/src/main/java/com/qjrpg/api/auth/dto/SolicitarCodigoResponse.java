package com.qjrpg.api.auth.dto;

// ATENCAO: codigoParaTeste so existe porque ainda nao ha envio real de e-mail
// configurado (sem SMTP). Antes de qualquer uso real, remova este campo e
// implemente o envio de fato (ver JavaMailSender do Spring).
public record SolicitarCodigoResponse(String mensagem, String codigoParaTeste) {}
