package com.qjrpg.api.notificacao;

import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementacao interna (log) - registra a notificacao mas nao envia push de
 * verdade. Envio real via Firebase Cloud Messaging exige um projeto Firebase
 * proprio (com credenciais que precisam ser criadas por quem for publicar o
 * app); esta classe e o unico ponto do sistema que precisaria mudar quando
 * isso for configurado, o resto do codigo so depende da interface.
 */
@Service
public class NotificacaoServiceImpl implements NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoServiceImpl.class);
    private final UsuarioRepository usuarioRepository;

    public NotificacaoServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void notificar(UUID usuarioId, String mensagem) {
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            if (!usuario.isNotificacoesPushAtivas()) return;
            log.info("[NOTIFICACAO] Para {}: {}", usuario.getEmail(), mensagem);
        });
    }

    @Override
    public void notificarAdministracao(String mensagem) {
        usuarioRepository.findAll().stream()
                .filter(u -> u.getPapel() == PapelUsuario.ADMIN || u.getPapel() == PapelUsuario.MODERADOR)
                .forEach(u -> notificar(u.getId(), mensagem));
    }
}
