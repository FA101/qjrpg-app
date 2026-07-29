package com.qjrpg.api.evento;

import com.qjrpg.api.evento.dto.EventoRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventoServiceImplTest {

    @Mock private EventoRepository repository;
    private EventoService service;

    @BeforeEach
    void configurar() { service = new EventoServiceImpl(repository); }

    @Test
    void deveCriarEvento() {
        EventoRequest request = new EventoRequest("QJRPG Agosto", LocalDate.of(2026, 8, 15), "HUB Goias", null,
                StatusEvento.PLANEJADO, LocalTime.of(9, 0), LocalTime.of(22, 0));
        when(repository.save(any(Evento.class))).thenAnswer(c -> c.getArgument(0));

        Evento resultado = service.criar(request);

        assertThat(resultado.getNome()).isEqualTo("QJRPG Agosto");
        assertThat(resultado.getData()).isEqualTo(LocalDate.of(2026, 8, 15));
        verify(repository).save(any(Evento.class));
    }

    @Test
    void deveListarTodosOsEventos() {
        when(repository.findAll()).thenReturn(List.of(
                new Evento("Evento 1", LocalDate.of(2026, 9, 12), "Local 1", null, StatusEvento.PUBLICADO, null, null)));

        assertThat(service.listarTodos()).hasSize(1);
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(idInexistente));
    }

    @Test
    void deveAtualizarEvento() {
        UUID id = UUID.randomUUID();
        Evento existente = new Evento("Antigo", LocalDate.of(2026, 1, 1), "Local antigo", null, StatusEvento.PLANEJADO, null, null);
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Evento.class))).thenAnswer(c -> c.getArgument(0));

        EventoRequest request = new EventoRequest("Novo nome", LocalDate.of(2026, 10, 3), "Novo local", null,
                StatusEvento.PUBLICADO, LocalTime.of(9, 0), LocalTime.of(22, 0));
        Evento atualizado = service.atualizar(id, request);

        assertThat(atualizado.getNome()).isEqualTo("Novo nome");
        assertThat(atualizado.getData()).isEqualTo(LocalDate.of(2026, 10, 3));
    }

    @Test
    void deveExcluirEvento() {
        UUID id = UUID.randomUUID();
        Evento existente = new Evento("Evento", LocalDate.of(2026, 1, 1), "Local", null, StatusEvento.PLANEJADO, null, null);
        when(repository.findById(id)).thenReturn(Optional.of(existente));

        service.excluir(id);

        verify(repository).delete(existente);
    }
}
