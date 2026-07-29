package com.qjrpg.api.conteudo;
import com.qjrpg.api.conteudo.dto.ConteudoRequest;
import com.qjrpg.api.conteudo.dto.ConteudoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conteudos")
public class ConteudoInstitucionalController {
    private final ConteudoInstitucionalService service;
    public ConteudoInstitucionalController(ConteudoInstitucionalService service) { this.service = service; }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ConteudoResponse> criar(@Valid @RequestBody ConteudoRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ConteudoResponse.de(service.criar(r)));
    }

    @GetMapping
    public List<ConteudoResponse> listar() {
        return service.listarTodos().stream().map(ConteudoResponse::de).collect(Collectors.toList());
    }

    @GetMapping("/{secao}")
    public ConteudoResponse buscar(@PathVariable String secao) { return ConteudoResponse.de(service.buscarPorSecao(secao)); }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ConteudoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody ConteudoRequest r) {
        return ConteudoResponse.de(service.atualizar(id, r));
    }
}
