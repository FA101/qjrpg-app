package com.qjrpg.api.mensagem;
import com.qjrpg.api.mensagem.dto.MensagemRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class MensagemServiceImpl implements MensagemService {
    private final MensagemRepository repository;
    public MensagemServiceImpl(MensagemRepository repository) { this.repository = repository; }

    @Override
    public Mensagem enviar(MensagemRequest r) {
        return repository.save(new Mensagem(r.mesaId(), r.autorId(), r.respostaDeId(), r.conteudo()));
    }

    @Override
    public List<Mensagem> listarPorMesa(UUID mesaId) { return repository.findByMesaIdOrderByDataHoraAsc(mesaId); }

    @Override
    public void excluir(UUID id) {
        Mensagem m = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mensagem nao encontrada: " + id));
        repository.delete(m);
    }
}
