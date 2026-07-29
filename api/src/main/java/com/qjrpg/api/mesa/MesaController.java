package com.qjrpg.api.mesa;
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

    @PostMapping
    public ResponseEntity<MesaResponse> ofertar(@Valid @RequestBody MesaRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paraResponse(service.ofertar(r)));
    }

    @GetMapping
    public List<MesaResponse> listar(@RequestParam UUID eventoId) {
        return service.listarPorEvento(eventoId).stream().map(this::paraResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MesaResponse buscar(@PathVariable UUID id) { return paraResponse(service.buscarPorId(id)); }

    @PatchMapping("/{id}/status")
    public MesaResponse atualizarStatus(@PathVariable UUID id, @Valid @RequestBody MesaStatusRequest r) {
        return paraResponse(service.atualizarStatus(id, r.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
