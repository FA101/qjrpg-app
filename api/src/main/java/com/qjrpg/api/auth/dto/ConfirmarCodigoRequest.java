package com.qjrpg.api.auth.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ConfirmarCodigoRequest(
        @NotBlank @Email String email, @NotBlank String codigo, String nome, String celular, String apelido,
        LocalDate dataNascimento, String sexo, String responsavelEmail) {}
