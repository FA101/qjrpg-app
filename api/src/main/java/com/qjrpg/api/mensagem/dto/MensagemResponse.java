package com.qjrpg.api.mensagem.dto;
import com.qjrpg.api.mensagem.Mensagem;
import java.time.Instant;
import java.util.UUID;
public record MensagemResponse(UUID id, UUID mesaId, UUID autorId, UUID respostaDeId, String conteudo, Instant dataHora) {
    public static MensagemResponse de(Mensagem m) {
        return new MensagemResponse(m.getId(), m.getMesaId(), m.getAutorId(), m.getRespostaDeId(),
                m.getConteudo(), m.getDataHora());
    }
}
