package com.qjrpg.api.moderacao;

import com.qjrpg.api.moderacao.dto.DenunciaRequest;
import com.qjrpg.api.moderacao.dto.DenunciaResponse;
import com.qjrpg.api.moderacao.dto.DenunciaStatusRequest;
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
@RequestMapping("/api/denuncias")
public class DenunciaController {

    private final DenunciaService service;

    public DenunciaController(DenunciaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DenunciaResponse> denunciar(@Valid @RequestBody DenunciaRequest r, Authentication auth) {
        UUID denuncianteId = UUID.fromString(auth.getPrincipal().toString());
        Denuncia denuncia = service.denunciar(r.usuarioDenunciadoId(), denuncianteId, r.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(DenunciaResponse.de(denuncia));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @GetMapping
    public List<DenunciaResponse> listarPendentes() {
        return service.listarPendentes().stream().map(DenunciaResponse::de).collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @PatchMapping("/{id}/status")
    public DenunciaResponse atualizarStatus(@PathVariable UUID id, @Valid @RequestBody DenunciaStatusRequest r) {
        return DenunciaResponse.de(service.atualizarStatus(id, r.status()));
    }
}
