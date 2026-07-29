package com.qjrpg.api.auth.seguranca;

import com.qjrpg.api.usuario.PapelUsuario;
import java.util.UUID;

public interface JwtService {
    String gerarToken(UUID usuarioId, PapelUsuario papel);
    UUID extrairUsuarioId(String token);
}
