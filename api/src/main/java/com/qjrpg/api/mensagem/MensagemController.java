package com.qjrpg.api.mensagem;
import com.qjrpg.api.mensagem.dto.MensagemRequest;
import com.qjrpg.api.mensagem.dto.MensagemResponse;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public MensagemController(MensagemService service, UsuarioRepository usuarioRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
    }

    private MensagemResponse paraResponse(Mensagem m) {
        String nomeExibicao = usuarioRepository.findById(m.getAutorId())
                .map(Usuario::nomeExibicao).orElse("Usuario");
        return MensagemResponse.de(m, nomeExibicao);
    }

    @PostMapping
    public ResponseEntity<MensagemResponse> enviar(@Valid @RequestBody MensagemRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paraResponse(service.enviar(r)));
    }

    @GetMapping
    public List<MensagemResponse> listar(@RequestParam UUID mesaId) {
        return service.listarPorMesa(mesaId).stream().map(this::paraResponse).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
