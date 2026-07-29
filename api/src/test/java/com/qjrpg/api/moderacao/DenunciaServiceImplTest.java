package com.qjrpg.api.moderacao;

import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DenunciaServiceImplTest {

    @Mock private DenunciaRepository repository;
    @Mock private UsuarioRepository usuarioRepository;
    private DenunciaService service;

    @BeforeEach
    void configurar() {
        service = new DenunciaServiceImpl(repository, usuarioRepository);
    }

    @Test
    void deveLimparApelidoQuandoDenunciaForProcedente() {
        UUID denunciadoId = UUID.randomUUID();
        Denuncia denuncia = new Denuncia(denunciadoId, UUID.randomUUID(), "Apelido ofensivo");
        Usuario denunciado = new Usuario("jogador@exemplo.com", PapelUsuario.COMUM);
        denunciado.definirApelido("ApelidoRuim");

        when(repository.findById(denuncia.getId())).thenReturn(Optional.of(denuncia));
        when(repository.save(any(Denuncia.class))).thenAnswer(c -> c.getArgument(0));
        when(usuarioRepository.findById(denunciadoId)).thenReturn(Optional.of(denunciado));

        service.atualizarStatus(denuncia.getId(), StatusDenuncia.PROCEDENTE);

        assertThat(denunciado.getApelido()).isNull();
    }

    @Test
    void naoDeveAlterarApelidoQuandoDenunciaForImprocedente() {
        UUID denunciadoId = UUID.randomUUID();
        Denuncia denuncia = new Denuncia(denunciadoId, UUID.randomUUID(), "Motivo qualquer");
        Usuario denunciado = new Usuario("jogador@exemplo.com", PapelUsuario.COMUM);
        denunciado.definirApelido("ApelidoOk");

        when(repository.findById(denuncia.getId())).thenReturn(Optional.of(denuncia));
        when(repository.save(any(Denuncia.class))).thenAnswer(c -> c.getArgument(0));

        service.atualizarStatus(denuncia.getId(), StatusDenuncia.IMPROCEDENTE);

        assertThat(denunciado.getApelido()).isEqualTo("ApelidoOk");
    }
}
