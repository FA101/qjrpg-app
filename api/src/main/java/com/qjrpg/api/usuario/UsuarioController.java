package com.qjrpg.api.usuario;

import com.qjrpg.api.moderacao.FiltroDePalavrasService;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.usuario.dto.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** Autoatendimento: cada usuario ve e edita so o proprio perfil. */
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
            if (r.apelido().length() < com.qjrpg.api.auth.AuthServiceImpl.APELIDO_MIN_CARACTERES) {
                throw new RegraDeNegocioException("Apelido deve ter pelo menos "
                        + com.qjrpg.api.auth.AuthServiceImpl.APELIDO_MIN_CARACTERES + " caracteres");
            }
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
        if (r.mostrarNomeReal() != null) usuario.definirPreferenciaNome(r.mostrarNomeReal());
        repository.save(usuario);
        return UsuarioResponse.de(usuario);
    }

    // RF37
    @PutMapping("/me/notificacoes")
    public UsuarioResponse atualizarNotificacoes(@Valid @RequestBody NotificacoesUpdateRequest r, Authentication auth) {
        Usuario usuario = buscar(auth);
        usuario.definirNotificacoesPushAtivas(r.ativas());
        repository.save(usuario);
        return UsuarioResponse.de(usuario);
    }

    // RF38
    @PutMapping("/me/suspender")
    public UsuarioResponse suspender(@Valid @RequestBody SuspenderRequest r, Authentication auth) {
        Usuario usuario = buscar(auth);
        usuario.suspenderPor(r.dias());
        repository.save(usuario);
        return UsuarioResponse.de(usuario);
    }

    @PutMapping("/me/reativar")
    public UsuarioResponse reativar(Authentication auth) {
        Usuario usuario = buscar(auth);
        usuario.reativar();
        repository.save(usuario);
        return UsuarioResponse.de(usuario);
    }

    // RF39 (LGPD): anonimiza em vez de apagar a linha, para nao quebrar referencias de UUID solto
    @DeleteMapping("/me")
    public void excluirConta(Authentication auth) {
        Usuario usuario = buscar(auth);
        usuario.excluirDadosPessoais();
        repository.save(usuario);
    }

    // RF34-36: vinculos de menores pendentes de confirmacao pelo responsavel
    @GetMapping("/vinculos-pendentes")
    public List<UsuarioResponse> vinculosPendentes(Authentication auth) {
        Usuario responsavel = buscar(auth);
        return repository.findByResponsavelEmailAndVinculoResponsavelConfirmadoFalse(responsavel.getEmail())
                .stream().map(UsuarioResponse::de).collect(Collectors.toList());
    }

    // RF36b/RF36c: todos os menores vinculados a mim (autorizados ou suspensos)
    @GetMapping("/vinculos")
    public List<UsuarioResponse> meusVinculos(Authentication auth) {
        Usuario responsavel = buscar(auth);
        return repository.findByResponsavelEmailIgnoreCase(responsavel.getEmail()).stream()
                .filter(Usuario::isMenorDeIdade)
                .map(UsuarioResponse::de).collect(Collectors.toList());
    }

    @PatchMapping("/vinculos/{menorId}/confirmar")
    public UsuarioResponse confirmarVinculo(@PathVariable UUID menorId, Authentication auth) {
        Usuario responsavel = buscar(auth);
        Usuario menor = repository.findById(menorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + menorId));
        if (!responsavel.getEmail().equalsIgnoreCase(menor.getResponsavelEmail())) {
            throw new RegraDeNegocioException("Voce nao e o responsavel indicado por este usuario");
        }
        menor.confirmarVinculoResponsavel();
        repository.save(menor);
        return UsuarioResponse.de(menor);
    }

    @PatchMapping("/vinculos/{menorId}/revogar")
    public UsuarioResponse revogarVinculo(@PathVariable UUID menorId, Authentication auth) {
        Usuario responsavel = buscar(auth);
        Usuario menor = repository.findById(menorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + menorId));
        if (!responsavel.getEmail().equalsIgnoreCase(menor.getResponsavelEmail())) {
            throw new RegraDeNegocioException("Voce nao e o responsavel indicado por este usuario");
        }
        menor.revogarVinculoResponsavel();
        repository.save(menor);
        return UsuarioResponse.de(menor);
    }

    private Usuario buscar(Authentication auth) {
        UUID id = UUID.fromString(auth.getPrincipal().toString());
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
    }
}
