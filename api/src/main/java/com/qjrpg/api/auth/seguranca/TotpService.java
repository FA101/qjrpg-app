package com.qjrpg.api.auth.seguranca;

public interface TotpService {
    String gerarSegredo();
    boolean validarCodigo(String segredo, String codigo);
    String gerarOtpAuthUri(String segredo, String emailUsuario);
}
