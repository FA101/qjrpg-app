package com.qjrpg.api.usuario;

import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.usuario.dto.AtivoUpdateRequest;
import com.qjrpg.api.usuario.dto.PapelUpdateRequest;
import com.qjrpg.api.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Endpoints administrativos sobre outros usuarios (RF22-RF25).
 * Promover/demover Admin ou Moderador: exclusivo de Admin (garantido pelo
 * @PreAuthorize da classe - Moderador nunca acessa estes metodos).
 * Suspender/ativar conta comum: Admin ou Moderador (mas Moderador nao pode
 * mexer em Admin/Moderador, verificado no metodo).
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioAdminController {

    private final UsuarioRepository repository;

    public UsuarioAdminController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @GetMapping
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::de).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/papel")
    public UsuarioResponse alterarPapel(@PathVariable UUID id, @Valid @RequestBody PapelUpdateRequest r) {
        Usuario usuario = buscar(id);
        usuario.definirPapel(r.papel());
        repository.save(usuario);
        return UsuarioResponse.de(usuario);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PatchMapping("/{id}/ativo")
    public UsuarioResponse alterarAtivo(@PathVariable UUID id, @Valid @RequestBody AtivoUpdateRequest r,
                                        Authentication auth) {
        Usuario usuario = buscar(id);
        boolean chamadorEhModerador = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MODERADOR"));
        boolean alvoEhAdminOuModerador = usuario.getPapel() != PapelUsuario.COMUM;
        if (chamadorEhModerador && alvoEhAdminOuModerador) {
            throw new RegraDeNegocioException("Moderador nao pode suspender/ativar Admin ou outro Moderador");
        }
        usuario.definirAtivo(r.ativo());
        repository.save(usuario);
        return UsuarioResponse.de(usuario);
    }

    private Usuario buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + id));
    }
}
