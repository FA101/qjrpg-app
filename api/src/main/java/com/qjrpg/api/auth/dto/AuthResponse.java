package com.qjrpg.api.auth.dto;
import com.qjrpg.api.usuario.PapelUsuario;
import java.util.UUID;
public record AuthResponse(String token, UUID usuarioId, String nome, String email, PapelUsuario papel) {}
