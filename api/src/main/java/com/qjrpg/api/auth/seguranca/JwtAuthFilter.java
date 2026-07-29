package com.qjrpg.api.auth.seguranca;

import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Le o header Authorization: Bearer <token>, valida e, se ok, autentica a
 * requisicao. Se o token for invalido/ausente, simplesmente segue sem
 * autenticar - quem decide se isso e permitido ou nao e o SecurityConfig.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                UUID usuarioId = jwtService.extrairUsuarioId(token);
                usuarioRepository.findById(usuarioId).ifPresent(this::autenticar);
            } catch (Exception ignorado) {
                // token invalido/expirado: segue sem autenticar
            }
        }
        filterChain.doFilter(request, response);
    }

    private void autenticar(Usuario usuario) {
        if (!usuario.isAtivo()) return;
        var authority = new SimpleGrantedAuthority("ROLE_" + usuario.getPapel().name());
        var auth = new UsernamePasswordAuthenticationToken(usuario.getId(), null, List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
