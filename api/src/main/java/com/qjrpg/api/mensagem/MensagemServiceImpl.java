package com.qjrpg.api.mensagem;
import com.qjrpg.api.mensagem.dto.MensagemRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class MensagemServiceImpl implements MensagemService {
    private final MensagemRepository repository;
    public MensagemServiceImpl(MensagemRepository repository) { this.repository = repository; }

    @Override
    public Mensagem enviar(MensagemRequest r, UUID autorId) {
        if (r.respostaDeId() != null) { // RF13: resposta encadeada so dentro da mesma mesa
            Mensagem original = buscarPorId(r.respostaDeId());
            if (!original.getMesaId().equals(r.mesaId())) {
                throw new RegraDeNegocioException("A mensagem respondida pertence a outra mesa");
            }
        }
        return repository.save(new Mensagem(r.mesaId(), autorId, r.respostaDeId(), r.conteudo()));
    }

    @Override
    public Mensagem buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Mensagem nao encontrada: " + id));
    }

    @Override
    public List<Mensagem> listarPorMesa(UUID mesaId) { return repository.findByMesaIdOrderByDataHoraAsc(mesaId); }

    @Override
    public void excluir(UUID id) {
        repository.delete(buscarPorId(id));
    }
}
