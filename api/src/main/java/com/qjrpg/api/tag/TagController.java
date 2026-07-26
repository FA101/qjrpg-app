package com.qjrpg.api.tag;

import com.qjrpg.api.tag.dto.TagRequest;
import com.qjrpg.api.tag.dto.TagResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TagResponse> criar(@Valid @RequestBody TagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(TagResponse.deTag(service.criar(request)));
    }

    @GetMapping
    public List<TagResponse> listar() {
        return service.listarTodos().stream().map(TagResponse::deTag).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TagResponse buscar(@PathVariable UUID id) {
        return TagResponse.deTag(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public TagResponse atualizar(@PathVariable UUID id, @Valid @RequestBody TagRequest request) {
        return TagResponse.deTag(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
