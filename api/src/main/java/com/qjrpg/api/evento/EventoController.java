package com.qjrpg.api.evento;

import com.qjrpg.api.evento.dto.EventoRequest;
import com.qjrpg.api.evento.dto.EventoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// RF04: so Admin cria/edita/exclui eventos. RF05: qualquer usuario autenticado visualiza.
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) { this.service = service; }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EventoResponse> criar(@Valid @RequestBody EventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(EventoResponse.deEvento(service.criar(request)));
    }

    @GetMapping
    public List<EventoResponse> listar() {
        return service.listarTodos().stream().map(EventoResponse::deEvento).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EventoResponse buscar(@PathVariable UUID id) {
        return EventoResponse.deEvento(service.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public EventoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody EventoRequest request) {
        return EventoResponse.deEvento(service.atualizar(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
