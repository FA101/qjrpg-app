package com.qjrpg.api.mensagem;
import com.qjrpg.api.mensagem.dto.MensagemRequest;
import com.qjrpg.api.mensagem.dto.MensagemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {
    private final MensagemService service;
    public MensagemController(MensagemService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<MensagemResponse> enviar(@Valid @RequestBody MensagemRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(MensagemResponse.de(service.enviar(r)));
    }

    @GetMapping
    public List<MensagemResponse> listar(@RequestParam UUID mesaId) {
        return service.listarPorMesa(mesaId).stream().map(MensagemResponse::de).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
