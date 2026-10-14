package com.qjrpg.api.auth.dto;
import com.qjrpg.api.usuario.PapelUsuario;
import java.util.UUID;

// token vem nulo quando totpPendente=true: o front-end deve pedir o codigo do app
// autenticador e chamar POST /api/auth/2fa/validar enviando email, desafio2fa e codigo.
public record AuthResponse(String token, UUID usuarioId, String nome, String apelido, String email,
                            PapelUsuario papel, boolean vinculoResponsavelConfirmado, boolean totpPendente,
                            String desafio2fa) {}
