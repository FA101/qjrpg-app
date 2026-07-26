package com.qjrpg.api.link;
import com.qjrpg.api.link.dto.LinkUtilRequest;
import com.qjrpg.api.link.dto.LinkUtilResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/links-uteis")
public class LinkUtilController {
    private final LinkUtilService service;
    public LinkUtilController(LinkUtilService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<LinkUtilResponse> criar(@Valid @RequestBody LinkUtilRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(LinkUtilResponse.de(service.criar(r)));
    }

    @GetMapping
    public List<LinkUtilResponse> listar() {
        return service.listarTodos().stream().map(LinkUtilResponse::de).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public LinkUtilResponse atualizar(@PathVariable UUID id, @Valid @RequestBody LinkUtilRequest r) {
        return LinkUtilResponse.de(service.atualizar(id, r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
