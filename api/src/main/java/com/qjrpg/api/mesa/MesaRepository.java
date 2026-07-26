package com.qjrpg.api.mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface MesaRepository extends JpaRepository<Mesa, UUID> {
    List<Mesa> findByEventoId(UUID eventoId);
    List<Mesa> findByEventoIdAndGameMasterId(UUID eventoId, UUID gameMasterId);
}
