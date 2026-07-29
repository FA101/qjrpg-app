package com.qjrpg.api.candidatura;
import com.qjrpg.api.candidatura.dto.CandidaturaRequest;
import com.qjrpg.api.candidatura.dto.CandidaturaResponse;
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
@RequestMapping("/api/candidaturas")
public class CandidaturaController {
    private final CandidaturaService service;
    private final UsuarioRepository usuarioRepository;

    public CandidaturaController(CandidaturaService service, UsuarioRepository usuarioRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
    }

    private CandidaturaResponse paraResponse(Candidatura c) {
        String nomeExibicao = usuarioRepository.findById(c.getUsuarioId())
                .map(Usuario::nomeExibicao).orElse("Usuario");
        return CandidaturaResponse.de(c, nomeExibicao);
    }

    @PostMapping
    public ResponseEntity<CandidaturaResponse> candidatar(@Valid @RequestBody CandidaturaRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paraResponse(service.candidatar(r)));
    }

    @GetMapping
    public List<CandidaturaResponse> listar(@RequestParam UUID mesaId) {
        return service.listarPorMesa(mesaId).stream().map(this::paraResponse).collect(Collectors.toList());
    }

    @PatchMapping("/{id}/aceitar")
    public CandidaturaResponse aceitar(@PathVariable UUID id) { return paraResponse(service.aceitar(id)); }

    @PatchMapping("/{id}/recusar")
    public CandidaturaResponse recusar(@PathVariable UUID id) { return paraResponse(service.recusar(id)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
