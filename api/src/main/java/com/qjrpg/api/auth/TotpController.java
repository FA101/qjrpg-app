package com.qjrpg.api.auth;

import com.qjrpg.api.auth.seguranca.TotpService;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Endpoints de configuracao do 2FA - todos exigem estar logado (nao ficam
 * em /api/auth/**, que e publico; ficam em /api/2fa/**, que cai na regra
 * padrao "anyRequest().authenticated()" do SecurityConfig).
 */
@RestController
@RequestMapping("/api/2fa")
public class TotpController {

    private final TotpService totpService;
    private final UsuarioRepository usuarioRepository;

    public TotpController(TotpService totpService, UsuarioRepository usuarioRepository) {
        this.totpService = totpService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/iniciar")
    public Map<String, String> iniciar(Authentication auth) {
        Usuario usuario = buscar(auth);
        String segredo = totpService.gerarSegredo();
        usuario.definirTotpSecretPendente(segredo);
        usuarioRepository.save(usuario);
        return Map.of("segredo", segredo, "otpauthUri", totpService.gerarOtpAuthUri(segredo, usuario.getEmail()));
    }

    @PostMapping("/confirmar")
    public Map<String, Object> confirmar(@RequestBody Map<String, String> corpo, Authentication auth) {
        Usuario usuario = buscar(auth);
        String codigo = corpo.get("codigo");
        if (usuario.getTotpSecret() == null || !totpService.validarCodigo(usuario.getTotpSecret(), codigo)) {
            throw new RegraDeNegocioException("Codigo invalido. Confira o horario do seu celular e tente de novo.");
        }
        usuario.ativarTotp();
        usuarioRepository.save(usuario);
        return Map.of("totpAtivo", true);
    }

    @PostMapping("/desativar")
    public Map<String, Object> desativar(Authentication auth) {
        Usuario usuario = buscar(auth);
        usuario.desativarTotp();
        usuarioRepository.save(usuario);
        return Map.of("totpAtivo", false);
    }

    private Usuario buscar(Authentication auth) {
        UUID id = UUID.fromString(auth.getPrincipal().toString());
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }
}
