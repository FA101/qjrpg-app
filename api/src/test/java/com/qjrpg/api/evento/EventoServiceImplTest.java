package com.qjrpg.api.evento;

import com.qjrpg.api.evento.dto.EventoRequest;
import com.qjrpg.api.shared.exception.EventoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Nenhum destes testes toca um banco de dados real: o EventoRepository
 * e mockado, entao o teste roda em milissegundos e valida so a regra
 * de negocio do Service (RNF02 do PRD - CRUD testavel).
 */
@ExtendWith(MockitoExtension.class)
class EventoServiceImplTest {

    @Mock
    private EventoRepository repository;

    private EventoService service;

    @BeforeEach
    void configurar() {
        service = new EventoServiceImpl(repository);
    }

    @Test
    void deveCriarEvento() {
        EventoRequest request = new EventoRequest(
                "QJRPG Agosto", "HUB Goias", "https://maps.example.com", StatusEvento.PLANEJADO);
        when(repository.save(any(Evento.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Evento resultado = service.criar(request);

        assertThat(resultado.getNome()).isEqualTo("QJRPG Agosto");
        verify(repository).save(any(Evento.class));
    }

    @Test
    void deveListarTodosOsEventos() {
        when(repository.findAll()).thenReturn(List.of(
                new Evento("Evento 1", "Local 1", null, StatusEvento.PUBLICADO)
        ));

        List<Evento> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(EventoNaoEncontradoException.class, () -> service.buscarPorId(idInexistente));
    }

    @Test
    void deveAtualizarEvento() {
        UUID id = UUID.randomUUID();
        Evento existente = new Evento("Antigo", "Local antigo", null, StatusEvento.PLANEJADO);
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Evento.class))).thenAnswer(chamada -> chamada.getArgument(0));

        EventoRequest request = new EventoRequest("Novo nome", "Novo local", null, StatusEvento.PUBLICADO);
        Evento atualizado = service.atualizar(id, request);

        assertThat(atualizado.getNome()).isEqualTo("Novo nome");
        assertThat(atualizado.getStatus()).isEqualTo(StatusEvento.PUBLICADO);
    }

    @Test
    void deveExcluirEvento() {
        UUID id = UUID.randomUUID();
        Evento existente = new Evento("Evento", "Local", null, StatusEvento.PLANEJADO);
        when(repository.findById(id)).thenReturn(Optional.of(existente));

        service.excluir(id);

        verify(repository).delete(existente);
    }
}
