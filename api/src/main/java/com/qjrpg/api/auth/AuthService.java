package com.qjrpg.api.auth;
import java.time.LocalDate;

public interface AuthService {
    String solicitarCodigo(String email);
    AuthResultado confirmarCodigo(String email, String codigo, String nome, String celular, String apelido,
                                   LocalDate dataNascimento, String sexo, String responsavelEmail);
    AuthResultado validarSegundoFator(String email, String desafio2fa, String codigoTotp);
}
