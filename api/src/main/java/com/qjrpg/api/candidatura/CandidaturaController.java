package com.qjrpg.api.candidatura;
import com.qjrpg.api.auth.seguranca.UsuarioLogado;
import com.qjrpg.api.candidatura.dto.CandidaturaRequest;
import com.qjrpg.api.candidatura.dto.CandidaturaResponse;
import com.qjrpg.api.mesa.Mesa;
import com.qjrpg.api.mesa.MesaRepository;
import com.qjrpg.api.shared.exception.AcessoNegadoException;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaController {
    private final CandidaturaService service;
    private final UsuarioRepository usuarioRepository;
    private final MesaRepository mesaRepository;

    public CandidaturaController(CandidaturaService service, UsuarioRepository usuarioRepository,
                                  MesaRepository mesaRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
        this.mesaRepository = mesaRepository;
    }

    private CandidaturaResponse paraResponse(Candidatura c) {
        String nomeExibicao = usuarioRepository.findById(c.getUsuarioId())
                .map(Usuario::nomeExibicao).orElse("Usuario");
        return CandidaturaResponse.de(c, nomeExibicao);
    }

    private boolean ehGameMasterDaMesa(Candidatura candidatura, Authentication auth) {
        Mesa mesa = mesaRepository.findById(candidatura.getMesaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mesa nao encontrada"));
        return mesa.getGameMasterId().equals(UsuarioLogado.id(auth));
    }

    // RF11: so o Game Master da mesa (ou Admin/Moderador) aceita/recusa candidaturas dela.
    private void validarQuemPodeDecidir(Candidatura candidatura, Authentication auth) {
        if (!ehGameMasterDaMesa(candidatura, auth) && !UsuarioLogado.ehAdminOuModerador(auth)) {
            throw new AcessoNegadoException(
                    "Somente o Game Master da mesa (ou Admin/Moderador) pode decidir esta candidatura");
        }
    }

    // RF10/RF44: o candidato e sempre o usuario autenticado.
    @PostMapping
    public ResponseEntity<CandidaturaResponse> candidatar(@Valid @RequestBody CandidaturaRequest r,
                                                          Authentication auth) {
        Candidatura criada = service.candidatar(r.mesaId(), UsuarioLogado.id(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(paraResponse(criada));
    }

    @GetMapping
    public List<CandidaturaResponse> listar(@RequestParam UUID mesaId) {
        return service.listarPorMesa(mesaId).stream().map(this::paraResponse).collect(Collectors.toList());
    }

    @PatchMapping("/{id}/aceitar")
    public CandidaturaResponse aceitar(@PathVariable UUID id, Authentication auth) {
        validarQuemPodeDecidir(service.buscarPorId(id), auth);
        return paraResponse(service.aceitar(id));
    }

    @PatchMapping("/{id}/recusar")
    public CandidaturaResponse recusar(@PathVariable UUID id, Authentication auth) {
        validarQuemPodeDecidir(service.buscarPorId(id), auth);
        return paraResponse(service.recusar(id));
    }

    // RF47: remove o proprio candidato, o Game Master da mesa, Moderador ou Admin.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id, Authentication auth) {
        Candidatura candidatura = service.buscarPorId(id);
        boolean ehCandidato = candidatura.getUsuarioId().equals(UsuarioLogado.id(auth));
        if (!ehCandidato && !ehGameMasterDaMesa(candidatura, auth) && !UsuarioLogado.ehAdminOuModerador(auth)) {
            throw new AcessoNegadoException("Sem permissao para remover esta candidatura");
        }
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
