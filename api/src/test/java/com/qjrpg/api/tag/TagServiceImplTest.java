package com.qjrpg.api.tag;

import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import com.qjrpg.api.tag.dto.TagRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock private TagRepository repository;
    @Mock private UsuarioTagRepository usuarioTagRepository;

    private TagService service;
    private final UUID usuarioId = UUID.randomUUID();

    @BeforeEach
    void configurar() {
        service = new TagServiceImpl(repository, usuarioTagRepository);
    }

    private Tag tagComId(String nome, String cor, TipoTag tipo) {
        Tag t = new Tag(nome, cor, tipo, null);
        ReflectionTestUtils.setField(t, "id", UUID.randomUUID());
        return t;
    }

    // ---------- CRUD e RF27/RF28

    @Test
    void deveCriarTag() {
        when(repository.findByCorHex("#FF0000")).thenReturn(Optional.empty());
        when(repository.findByNome("Veterano")).thenReturn(Optional.empty());
        when(repository.save(any(Tag.class))).thenAnswer(c -> c.getArgument(0));

        Tag tag = service.criar(new TagRequest("Veterano", "#FF0000", TipoTag.CUSTOMIZADA, "manual"));

        assertThat(tag.getNome()).isEqualTo("Veterano");
    }

    @Test
    void deveRecusarCorDuplicada() {
        Tag existente = new Tag("Vendas", "#00FF00", TipoTag.FIXA, null);
        when(repository.findByCorHex("#00FF00")).thenReturn(Optional.of(existente));

        assertThrows(RegraDeNegocioException.class, () ->
                service.criar(new TagRequest("Apoiador", "#00FF00", TipoTag.FIXA, null)));
    }

    @Test
    void naoExcluiTagDoSistema() {
        Tag gm = tagComId("Game Master", "#6A1B9A", TipoTag.FIXA);
        when(repository.findById(gm.getId())).thenReturn(Optional.of(gm));

        assertThrows(RegraDeNegocioException.class, () -> service.excluir(gm.getId()));
        verify(repository, never()).delete(any());
    }

    @Test
    void excluirTagCustomizadaRemoveAtribuicoes() {
        Tag custom = tagComId("Veterano", "#123456", TipoTag.CUSTOMIZADA);
        UsuarioTag atrib = new UsuarioTag(usuarioId, custom.getId(), false, null);
        when(repository.findById(custom.getId())).thenReturn(Optional.of(custom));
        when(usuarioTagRepository.findByTagId(custom.getId())).thenReturn(List.of(atrib));

        service.excluir(custom.getId());

        verify(usuarioTagRepository).deleteAll(List.of(atrib));
        verify(repository).delete(custom);
    }

    // ---------- Tags do sistema

    @Test
    void garantirTagsDoSistemaCriaAsQuatroQuandoAusentes() {
        when(repository.findByNome(anyString())).thenReturn(Optional.empty());
        when(repository.findByCorHex(anyString())).thenReturn(Optional.empty());

        service.garantirTagsDoSistema();

        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(repository, times(4)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(Tag::getNome)
                .containsExactlyInAnyOrder("Game Master", "Vendas", "Apoiador", "Moderador");
        assertThat(captor.getAllValues()).extracting(Tag::getCorHex).doesNotHaveDuplicates();
    }

    @Test
    void tagDoSistemaUsaOutraCorSeAPadraoJaEstiverEmUso() {
        when(repository.findByNome(anyString())).thenReturn(Optional.empty());
        lenient().when(repository.findByCorHex(anyString())).thenReturn(Optional.empty());
        lenient().when(repository.findByCorHex("#6A1B9A")).thenReturn(Optional.of(new Tag("Roxa", "#6A1B9A", TipoTag.CUSTOMIZADA, null)));

        service.garantirTagsDoSistema();

        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(repository, times(4)).save(captor.capture());
        Tag gm = captor.getAllValues().stream().filter(t -> t.getNome().equals("Game Master")).findFirst().orElseThrow();
        assertThat(gm.getCorHex()).isNotEqualTo("#6A1B9A");
    }

    @Test
    void atribuicaoAutomaticaEIdempotente() { // RF09/RF15/RF19 + RF21
        Tag vendas = tagComId("Vendas", "#2E7D32", TipoTag.FIXA);
        when(repository.findByNome("Vendas")).thenReturn(Optional.of(vendas));
        when(usuarioTagRepository.existsByUsuarioIdAndTagId(usuarioId, vendas.getId())).thenReturn(false, true);

        service.atribuirTagDoSistema(usuarioId, TagSistema.VENDAS);
        service.atribuirTagDoSistema(usuarioId, TagSistema.VENDAS);

        verify(usuarioTagRepository, times(1)).save(any(UsuarioTag.class));
    }

    // ---------- Atribuicao manual (RF26)

    @Test
    void atribuicaoManualDeTagCustomizada() {
        Tag custom = tagComId("Veterano", "#123456", TipoTag.CUSTOMIZADA);
        UUID admin = UUID.randomUUID();
        when(repository.findById(custom.getId())).thenReturn(Optional.of(custom));
        when(usuarioTagRepository.existsByUsuarioIdAndTagId(usuarioId, custom.getId())).thenReturn(false);

        service.atribuirManual(usuarioId, custom.getId(), admin);

        ArgumentCaptor<UsuarioTag> captor = ArgumentCaptor.forClass(UsuarioTag.class);
        verify(usuarioTagRepository).save(captor.capture());
        assertThat(captor.getValue().isAutomatica()).isFalse();
        assertThat(captor.getValue().getAtribuidaPor()).isEqualTo(admin);
    }

    @Test
    void tagModeradorNaoPodeSerAtribuidaManualmente() {
        Tag mod = tagComId("Moderador", "#1565C0", TipoTag.FIXA);
        when(repository.findById(mod.getId())).thenReturn(Optional.of(mod));

        assertThrows(RegraDeNegocioException.class, () -> service.atribuirManual(usuarioId, mod.getId(), UUID.randomUUID()));
    }

    @Test
    void tagsDoUsuarioListaTodasAsAtribuidas() {
        Tag a = tagComId("Vendas", "#2E7D32", TipoTag.FIXA);
        Tag b = tagComId("Game Master", "#6A1B9A", TipoTag.FIXA);
        when(usuarioTagRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(
                new UsuarioTag(usuarioId, a.getId(), true, null), new UsuarioTag(usuarioId, b.getId(), true, null)));
        when(repository.findAllById(List.of(a.getId(), b.getId()))).thenReturn(List.of(a, b));

        assertThat(service.tagsDoUsuario(usuarioId)).containsExactly(a, b);
    }
}
