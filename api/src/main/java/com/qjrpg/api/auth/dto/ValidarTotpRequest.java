package com.qjrpg.api.auth.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record ValidarTotpRequest(@NotBlank @Email String email, @NotBlank String desafio2fa, @NotBlank String codigo) {}
