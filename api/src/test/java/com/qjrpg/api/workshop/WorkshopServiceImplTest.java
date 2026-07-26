package com.qjrpg.api.workshop;

import com.qjrpg.api.workshop.dto.WorkshopRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkshopServiceImplTest {
    @Mock private WorkshopRepository repository;
    private WorkshopService service;

    @BeforeEach
    void configurar() { service = new WorkshopServiceImpl(repository); }

    @Test
    void deveAprovarWorkshop() {
        UUID id = UUID.randomUUID();
        Workshop w = new Workshop(UUID.randomUUID(), UUID.randomUUID(), "Introducao a wargames", null, null);
        when(repository.findById(id)).thenReturn(Optional.of(w));
        when(repository.save(any(Workshop.class))).thenAnswer(c -> c.getArgument(0));

        Workshop atualizado = service.atualizarStatus(id, StatusWorkshop.APROVADO);

        assertThat(atualizado.getStatus()).isEqualTo(StatusWorkshop.APROVADO);
    }
}
