package com.qjrpg.api.auth;
import com.qjrpg.api.usuario.Usuario;

/**
 * Resultado de uma etapa de login. Quando o 2FA esta ativo, a primeira etapa
 * devolve token nulo e um desafio2fa, que precisa ser reapresentado junto do
 * codigo TOTP para concluir o login.
 */
public record AuthResultado(Usuario usuario, String token, String desafio2fa) {
    public static AuthResultado concluido(Usuario usuario, String token) {
        return new AuthResultado(usuario, token, null);
    }
    public static AuthResultado segundoFatorPendente(Usuario usuario, String desafio) {
        return new AuthResultado(usuario, null, desafio);
    }
}
