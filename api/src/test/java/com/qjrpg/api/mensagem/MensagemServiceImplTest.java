package com.qjrpg.api.mensagem;

import com.qjrpg.api.mensagem.dto.MensagemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MensagemServiceImplTest {
    @Mock private MensagemRepository repository;
    private MensagemService service;

    @BeforeEach
    void configurar() { service = new MensagemServiceImpl(repository); }

    @Test
    void deveEnviarMensagem() {
        when(repository.save(any(Mensagem.class))).thenAnswer(c -> c.getArgument(0));
        Mensagem m = service.enviar(new MensagemRequest(UUID.randomUUID(), UUID.randomUUID(), null, "Ola, tem vaga?"));
        assertThat(m.getConteudo()).isEqualTo("Ola, tem vaga?");
    }
}
