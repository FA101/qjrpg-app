package com.qjrpg.api.auth;

import com.qjrpg.api.auth.dto.AuthResponse;
import com.qjrpg.api.auth.dto.ConfirmarCodigoRequest;
import com.qjrpg.api.auth.dto.SolicitarCodigoRequest;
import com.qjrpg.api.auth.dto.SolicitarCodigoResponse;
import com.qjrpg.api.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/solicitar-codigo")
    public SolicitarCodigoResponse solicitarCodigo(@Valid @RequestBody SolicitarCodigoRequest r) {
        String codigo = authService.solicitarCodigo(r.email());
        return new SolicitarCodigoResponse(
                "Codigo gerado (modo desenvolvimento, sem envio real de e-mail ainda)", codigo);
    }

    @PostMapping("/confirmar-codigo")
    public AuthResponse confirmarCodigo(@Valid @RequestBody ConfirmarCodigoRequest r) {
        AuthResultado resultado = authService.confirmarCodigo(r.email(), r.codigo(), r.nome(), r.celular());
        Usuario u = resultado.usuario();
        return new AuthResponse(resultado.token(), u.getId(), u.getNome(), u.getEmail(), u.getPapel());
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "usuarioId", authentication.getPrincipal().toString(),
                "papel", authentication.getAuthorities().toString());
    }
}
