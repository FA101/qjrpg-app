package com.qjrpg.api.moderacao;

import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DenunciaServiceImpl implements DenunciaService {

    private final DenunciaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public DenunciaServiceImpl(DenunciaRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Denuncia denunciar(UUID usuarioDenunciadoId, UUID usuarioDenuncianteId, String motivo) {
        return repository.save(new Denuncia(usuarioDenunciadoId, usuarioDenuncianteId, motivo));
    }

    @Override
    public List<Denuncia> listarPendentes() {
        return repository.findByStatus(StatusDenuncia.PENDENTE);
    }

    @Override
    public Denuncia atualizarStatus(UUID id, StatusDenuncia status) {
        Denuncia denuncia = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Denuncia nao encontrada: " + id));
        denuncia.atualizarStatus(status);
        repository.save(denuncia);

        // Denuncia procedente: forca o usuario a escolher um novo apelido (RF de moderacao).
        if (status == StatusDenuncia.PROCEDENTE) {
            usuarioRepository.findById(denuncia.getUsuarioDenunciadoId()).ifPresent(usuario -> {
                usuario.limparApelido();
                usuarioRepository.save(usuario);
            });
        }
        return denuncia;
    }
}
