package com.qjrpg.api.usuario.dto;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import java.util.UUID;
public record UsuarioResponse(UUID id, String nomeExibicao, String apelido, String email,
                               PapelUsuario papel, boolean mostrarNomeReal) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.nomeExibicao(), u.getApelido(), u.getEmail(),
                u.getPapel(), u.isMostrarNomeReal());
    }
}
