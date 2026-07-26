package com.qjrpg.api.workshop;
import com.qjrpg.api.workshop.dto.WorkshopRequest;
import com.qjrpg.api.workshop.dto.WorkshopResponse;
import com.qjrpg.api.workshop.dto.WorkshopStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workshops")
public class WorkshopController {
    private final WorkshopService service;
    public WorkshopController(WorkshopService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<WorkshopResponse> propor(@Valid @RequestBody WorkshopRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkshopResponse.de(service.propor(r)));
    }

    @GetMapping
    public List<WorkshopResponse> listar(@RequestParam UUID eventoId) {
        return service.listarPorEvento(eventoId).stream().map(WorkshopResponse::de).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WorkshopResponse buscar(@PathVariable UUID id) { return WorkshopResponse.de(service.buscarPorId(id)); }

    @PatchMapping("/{id}/status")
    public WorkshopResponse atualizarStatus(@PathVariable UUID id, @Valid @RequestBody WorkshopStatusRequest r) {
        return WorkshopResponse.de(service.atualizarStatus(id, r.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
