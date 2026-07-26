package com.qjrpg.api.conteudo;

import com.qjrpg.api.conteudo.dto.ConteudoRequest;
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
class ConteudoInstitucionalServiceImplTest {
    @Mock private ConteudoInstitucionalRepository repository;
    private ConteudoInstitucionalService service;

    @BeforeEach
    void configurar() { service = new ConteudoInstitucionalServiceImpl(repository); }

    @Test
    void deveCriarConteudo() {
        when(repository.save(any(ConteudoInstitucional.class))).thenAnswer(c -> c.getArgument(0));
        ConteudoInstitucional c = service.criar(new ConteudoRequest("regras-gerais", "Regras do QJRPG",
                "1. O evento e publico...", UUID.randomUUID()));
        assertThat(c.getSecao()).isEqualTo("regras-gerais");
    }
}
