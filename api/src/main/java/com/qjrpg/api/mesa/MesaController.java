package com.qjrpg.api.mesa;
import com.qjrpg.api.auth.seguranca.UsuarioLogado;
import com.qjrpg.api.candidatura.CandidaturaRepository;
import com.qjrpg.api.candidatura.StatusCandidatura;
import com.qjrpg.api.mesa.dto.MesaRequest;
import com.qjrpg.api.mesa.dto.MesaResponse;
import com.qjrpg.api.mesa.dto.MesaStatusRequest;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.qjrpg.api.shared.exception.AcessoNegadoException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {
    private final MesaService service;
    private final UsuarioRepository usuarioRepository;
    private final CandidaturaRepository candidaturaRepository;

    public MesaController(MesaService service, UsuarioRepository usuarioRepository,
                           CandidaturaRepository candidaturaRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    private MesaResponse paraResponse(Mesa m) {
        String gameMasterNome = usuarioRepository.findById(m.getGameMasterId())
                .map(Usuario::nomeExibicao).orElse("Usuario");
        long aceitas = candidaturaRepository.countByMesaIdAndStatus(m.getId(), StatusCandidatura.ACEITA);
        int disponiveis = Math.max(m.getVagasTotais() - m.getVagasReservadas() - (int) aceitas, 0);
        return MesaResponse.de(m, gameMasterNome, disponiveis);
    }

    // RF07: qualquer usuario autenticado pode ofertar mesa.
    @PostMapping
    public ResponseEntity<MesaResponse> ofertar(@Valid @RequestBody MesaRequest r, Authentication auth) {
        Mesa mesa = service.ofertar(r, UsuarioLogado.id(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(paraResponse(mesa));
    }

    @GetMapping
    public List<MesaResponse> listar(@RequestParam UUID eventoId) {
        return service.listarPorEvento(eventoId).stream().map(this::paraResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MesaResponse buscar(@PathVariable UUID id) { return paraResponse(service.buscarPorId(id)); }

    // RF08: aprovar/recusar mesa e acao de Admin ou Moderador.
    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PatchMapping("/{id}/status")
    public MesaResponse atualizarStatus(@PathVariable UUID id, @Valid @RequestBody MesaStatusRequest r) {
        return paraResponse(service.atualizarStatus(id, r.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id, Authentication auth) {
        Mesa mesa = service.buscarPorId(id);
        boolean ehGameMaster = mesa.getGameMasterId().equals(UsuarioLogado.id(auth));
        if (!ehGameMaster && !UsuarioLogado.ehAdminOuModerador(auth)) {
            throw new AcessoNegadoException("Somente o Game Master da mesa (ou Admin/Moderador) pode exclui-la");
        }
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
