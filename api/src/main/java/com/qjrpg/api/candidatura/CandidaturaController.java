package com.qjrpg.api.candidatura;
import com.qjrpg.api.candidatura.dto.CandidaturaRequest;
import com.qjrpg.api.candidatura.dto.CandidaturaResponse;
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
    public CandidaturaController(CandidaturaService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CandidaturaResponse> candidatar(@Valid @RequestBody CandidaturaRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CandidaturaResponse.de(service.candidatar(r)));
    }

    @GetMapping
    public List<CandidaturaResponse> listar(@RequestParam UUID mesaId) {
        return service.listarPorMesa(mesaId).stream().map(CandidaturaResponse::de).collect(Collectors.toList());
    }

    @PatchMapping("/{id}/aceitar")
    public CandidaturaResponse aceitar(@PathVariable UUID id) { return CandidaturaResponse.de(service.aceitar(id)); }

    @PatchMapping("/{id}/recusar")
    public CandidaturaResponse recusar(@PathVariable UUID id) { return CandidaturaResponse.de(service.recusar(id)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
