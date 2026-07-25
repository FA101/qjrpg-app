package com.qjrpg.api.evento;

import com.qjrpg.api.evento.dto.EventoRequest;
import com.qjrpg.api.evento.dto.EventoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> criar(@Valid @RequestBody EventoRequest request) {
        Evento criado = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventoResponse.deEvento(criado));
    }

    @GetMapping
    public List<EventoResponse> listar() {
        return service.listarTodos().stream()
                .map(EventoResponse::deEvento)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EventoResponse buscar(@PathVariable UUID id) {
        return EventoResponse.deEvento(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public EventoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody EventoRequest request) {
        return EventoResponse.deEvento(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
