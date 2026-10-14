package com.qjrpg.api.auth.seguranca;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * RF30: 2FA por app autenticador, padrao aberto TOTP (RFC 6238) - compativel
 * com Google Authenticator, Microsoft Authenticator, Authy etc. Nao gera QR
 * code (evita depender de outra biblioteca externa arriscada) - o segredo e
 * a URI otpauth:// sao suficientes para adicionar manualmente na maioria
 * dos apps autenticadores.
 */
@Service
public class TotpServiceImpl implements TotpService {

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeVerifier codeVerifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());

    @Override
    public String gerarSegredo() {
        return secretGenerator.generate();
    }

    @Override
    public boolean validarCodigo(String segredo, String codigo) {
        if (segredo == null || codigo == null) return false;
        return codeVerifier.isValidCode(segredo, codigo);
    }

    @Override
    public String gerarOtpAuthUri(String segredo, String emailUsuario) {
        String label = URLEncoder.encode("QJRPG:" + emailUsuario, StandardCharsets.UTF_8);
        return "otpauth://totp/" + label + "?secret=" + segredo + "&issuer=QJRPG";
    }
}
