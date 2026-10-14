package com.qjrpg.api.tag;

import com.qjrpg.api.auth.seguranca.UsuarioLogado;
import com.qjrpg.api.shared.exception.AcessoNegadoException;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.tag.dto.TagResponse;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Tags de um usuario (RF21, RF26, RF25).
 * Leitura: qualquer usuario autenticado (tags sao publicas no perfil).
 * Escrita: Admin em qualquer usuario; Moderador somente em usuarios comuns.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioTagController {

    private final TagService tagService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioTagController(TagService tagService, UsuarioRepository usuarioRepository) {
        this.tagService = tagService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/me/tags")
    public List<TagResponse> minhasTags(Authentication auth) {
        return paraResponse(tagService.tagsDoUsuario(UsuarioLogado.id(auth)));
    }

    @GetMapping("/{usuarioId}/tags")
    public List<TagResponse> tagsDoUsuario(@PathVariable UUID usuarioId) {
        buscarUsuario(usuarioId);
        return paraResponse(tagService.tagsDoUsuario(usuarioId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PostMapping("/{usuarioId}/tags/{tagId}")
    public List<TagResponse> atribuir(@PathVariable UUID usuarioId, @PathVariable UUID tagId, Authentication auth) {
        validarPermissaoSobreAlvo(buscarUsuario(usuarioId), auth);
        tagService.atribuirManual(usuarioId, tagId, UsuarioLogado.id(auth));
        return paraResponse(tagService.tagsDoUsuario(usuarioId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @DeleteMapping("/{usuarioId}/tags/{tagId}")
    public ResponseEntity<Void> remover(@PathVariable UUID usuarioId, @PathVariable UUID tagId, Authentication auth) {
        validarPermissaoSobreAlvo(buscarUsuario(usuarioId), auth);
        tagService.removerManual(usuarioId, tagId);
        return ResponseEntity.noContent().build();
    }

    // RF25 + matriz 4.1: Moderador so altera tags de usuarios comuns.
    private void validarPermissaoSobreAlvo(Usuario alvo, Authentication auth) {
        if (!UsuarioLogado.ehAdmin(auth) && alvo.getPapel() != PapelUsuario.COMUM) {
            throw new AcessoNegadoException("Moderador nao pode alterar tags de Administradores ou Moderadores");
        }
    }

    private Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + id));
    }

    private static List<TagResponse> paraResponse(List<Tag> tags) {
        return tags.stream().map(TagResponse::deTag).toList();
    }
}
