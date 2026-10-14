package com.qjrpg.api.notificacao;
import java.util.UUID;

public interface NotificacaoService {
    void notificar(UUID usuarioId, String mensagem);
    void notificarAdministracao(String mensagem);
}
