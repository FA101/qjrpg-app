package com.qjrpg.api.auth;

import com.qjrpg.api.auth.dto.*;
import com.qjrpg.api.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final boolean exporCodigo;

    public AuthController(AuthService authService,
                          @Value("${app.auth.expor-codigo:false}") boolean exporCodigo) {
        this.authService = authService;
        this.exporCodigo = exporCodigo;
    }

    @PostMapping("/solicitar-codigo")
    public SolicitarCodigoResponse solicitarCodigo(@Valid @RequestBody SolicitarCodigoRequest r) {
        String codigo = authService.solicitarCodigo(r.email());
        if (exporCodigo) {
            return new SolicitarCodigoResponse(
                    "Codigo gerado (modo desenvolvimento, sem envio real de e-mail)", codigo);
        }
        return new SolicitarCodigoResponse("Codigo enviado para o e-mail informado", null);
    }

    @PostMapping("/confirmar-codigo")
    public AuthResponse confirmarCodigo(@Valid @RequestBody ConfirmarCodigoRequest r) {
        AuthResultado resultado = authService.confirmarCodigo(r.email(), r.codigo(), r.nome(), r.celular(),
                r.apelido(), r.dataNascimento(), r.sexo(), r.responsavelEmail());
        Usuario u = resultado.usuario();
        boolean totpPendente = resultado.token() == null;
        return new AuthResponse(resultado.token(), u.getId(), u.getNome(), u.getApelido(), u.getEmail(),
                u.getPapel(), u.isVinculoResponsavelConfirmado(), totpPendente, resultado.desafio2fa());
    }

    @PostMapping("/2fa/validar")
    public AuthResponse validarTotp(@Valid @RequestBody ValidarTotpRequest r) {
        AuthResultado resultado = authService.validarSegundoFator(r.email(), r.desafio2fa(), r.codigo());
        Usuario u = resultado.usuario();
        return new AuthResponse(resultado.token(), u.getId(), u.getNome(), u.getApelido(), u.getEmail(),
                u.getPapel(), u.isVinculoResponsavelConfirmado(), false, null);
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "usuarioId", authentication.getPrincipal().toString(),
                "papel", authentication.getAuthorities().toString());
    }
}
