package com.qjrpg.api.usuario;

import com.qjrpg.api.moderacao.FiltroDePalavrasService;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.usuario.dto.PerfilUpdateRequest;
import com.qjrpg.api.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/** Autoatendimento: cada usuario ve e edita so o proprio perfil (nunca o de outro). */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;
    private final FiltroDePalavrasService filtro;

    public UsuarioController(UsuarioRepository repository, FiltroDePalavrasService filtro) {
        this.repository = repository;
        this.filtro = filtro;
    }

    @GetMapping("/me")
    public UsuarioResponse meuPerfil(Authentication auth) {
        return UsuarioResponse.de(buscar(auth));
    }

    @PutMapping("/me")
    public UsuarioResponse atualizarPerfil(@Valid @RequestBody PerfilUpdateRequest r, Authentication auth) {
        Usuario usuario = buscar(auth);

        if (r.apelido() != null && !r.apelido().equals(usuario.getApelido())) {
            if (filtro.contemPalavraProibida(r.apelido())) {
                throw new RegraDeNegocioException("Apelido contem termo nao permitido");
            }
            repository.findByApelido(r.apelido()).ifPresent(outro -> {
                if (!outro.getId().equals(usuario.getId())) {
                    throw new RegraDeNegocioException("Apelido ja esta em uso");
                }
            });
            usuario.definirApelido(r.apelido());
        }
        if (r.mostrarNomeReal() != null) {
            usuario.definirPreferenciaNome(r.mostrarNomeReal());
        }
        repository.save(usuario);
        return UsuarioResponse.de(usuario);
    }

    private Usuario buscar(Authentication auth) {
        UUID id = UUID.fromString(auth.getPrincipal().toString());
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }
}
