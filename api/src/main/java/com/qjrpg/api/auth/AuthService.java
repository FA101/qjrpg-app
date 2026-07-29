package com.qjrpg.api.auth;

public interface AuthService {
    String solicitarCodigo(String email);
    AuthResultado confirmarCodigo(String email, String codigo, String nome, String celular);
}
