package com.qjrpg.api.mesa;
import com.qjrpg.api.mesa.dto.MesaRequest;
import com.qjrpg.api.mesa.dto.MesaResponse;
import com.qjrpg.api.mesa.dto.MesaStatusRequest;
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
    public MesaController(MesaService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<MesaResponse> ofertar(@Valid @RequestBody MesaRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(MesaResponse.de(service.ofertar(r)));
    }

    @GetMapping
    public List<MesaResponse> listar(@RequestParam UUID eventoId) {
        return service.listarPorEvento(eventoId).stream().map(MesaResponse::de).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MesaResponse buscar(@PathVariable UUID id) { return MesaResponse.de(service.buscarPorId(id)); }

    @PatchMapping("/{id}/status")
    public MesaResponse atualizarStatus(@PathVariable UUID id, @Valid @RequestBody MesaStatusRequest r) {
        return MesaResponse.de(service.atualizarStatus(id, r.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
