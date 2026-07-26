package com.qjrpg.api.produto;
import com.qjrpg.api.produto.dto.ProdutoRequest;
import com.qjrpg.api.produto.dto.ProdutoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    private final ProdutoService service;
    public ProdutoController(ProdutoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.de(service.criar(r)));
    }

    @GetMapping
    public List<ProdutoResponse> listar(@RequestParam UUID eventoId) {
        return service.listarPorEvento(eventoId).stream().map(ProdutoResponse::de).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable UUID id) { return ProdutoResponse.de(service.buscarPorId(id)); }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody ProdutoRequest r) {
        return ProdutoResponse.de(service.atualizar(id, r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
