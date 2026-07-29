package com.qjrpg.api.auth.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record SolicitarCodigoRequest(@NotBlank @Email String email) {}
