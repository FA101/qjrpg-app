package com.qjrpg.api.workshop;
import com.qjrpg.api.auth.seguranca.UsuarioLogado;
import com.qjrpg.api.shared.exception.AcessoNegadoException;
import com.qjrpg.api.workshop.dto.WorkshopRequest;
import com.qjrpg.api.workshop.dto.WorkshopResponse;
import com.qjrpg.api.workshop.dto.WorkshopStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<WorkshopResponse> propor(@Valid @RequestBody WorkshopRequest r, Authentication auth) {
        Workshop proposto = service.propor(r, UsuarioLogado.id(auth));
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkshopResponse.de(proposto));
    }

    @GetMapping
    public List<WorkshopResponse> listar(@RequestParam UUID eventoId) {
        return service.listarPorEvento(eventoId).stream().map(WorkshopResponse::de).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WorkshopResponse buscar(@PathVariable UUID id) { return WorkshopResponse.de(service.buscarPorId(id)); }

    // RF18: aprovar/recusar workshop e acao de Admin ou Moderador.
    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PatchMapping("/{id}/status")
    public WorkshopResponse atualizarStatus(@PathVariable UUID id, @Valid @RequestBody WorkshopStatusRequest r) {
        return WorkshopResponse.de(service.atualizarStatus(id, r.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id, Authentication auth) {
        Workshop workshop = service.buscarPorId(id);
        boolean ehProponente = workshop.getUsuarioId().equals(UsuarioLogado.id(auth));
        if (!ehProponente && !UsuarioLogado.ehAdminOuModerador(auth)) {
            throw new AcessoNegadoException("Somente o proponente (ou Admin/Moderador) pode excluir o workshop");
        }
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
