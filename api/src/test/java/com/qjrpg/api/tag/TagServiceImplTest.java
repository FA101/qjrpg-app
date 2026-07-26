package com.qjrpg.api.tag;

import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.tag.dto.TagRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository repository;

    private TagService service;

    @BeforeEach
    void configurar() {
        service = new TagServiceImpl(repository);
    }

    @Test
    void deveCriarTag() {
        when(repository.findByCorHex("#FF0000")).thenReturn(Optional.empty());
        when(repository.save(any(Tag.class))).thenAnswer(c -> c.getArgument(0));

        Tag tag = service.criar(new TagRequest("Game Master", "#FF0000", TipoTag.FIXA, null));

        assertThat(tag.getNome()).isEqualTo("Game Master");
    }

    @Test
    void deveRecusarCorDuplicada() {
        Tag existente = new Tag("Vendas", "#00FF00", TipoTag.FIXA, null);
        when(repository.findByCorHex("#00FF00")).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () ->
                service.criar(new TagRequest("Apoiador", "#00FF00", TipoTag.FIXA, null)));
    }
}
