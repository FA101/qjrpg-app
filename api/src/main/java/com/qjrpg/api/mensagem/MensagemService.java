package com.qjrpg.api.mensagem;
import com.qjrpg.api.mensagem.dto.MensagemRequest;
import java.util.List;
import java.util.UUID;
public interface MensagemService {
    Mensagem enviar(MensagemRequest request);
    List<Mensagem> listarPorMesa(UUID mesaId);
    void excluir(UUID id);
}
