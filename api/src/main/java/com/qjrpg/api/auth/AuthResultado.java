package com.qjrpg.api.auth;
import com.qjrpg.api.usuario.Usuario;
public record AuthResultado(Usuario usuario, String token) {}
