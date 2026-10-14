package com.qjrpg.api.auth.seguranca;

import org.springframework.security.core.Authentication;

import java.util.UUID;

/** Leitura padronizada do usuario autenticado (principal = UUID, authority = ROLE_<PAPEL>). */
public final class UsuarioLogado {

    private UsuarioLogado() {}

    public static UUID id(Authentication auth) {
        return UUID.fromString(auth.getPrincipal().toString());
    }

    public static boolean temPapel(Authentication auth, String papel) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + papel));
    }

    public static boolean ehAdmin(Authentication auth) {
        return temPapel(auth, "ADMIN");
    }

    public static boolean ehAdminOuModerador(Authentication auth) {
        return temPapel(auth, "ADMIN") || temPapel(auth, "MODERADOR");
    }
}
