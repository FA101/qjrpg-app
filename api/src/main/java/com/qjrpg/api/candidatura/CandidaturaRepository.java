package com.qjrpg.api.candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface CandidaturaRepository extends JpaRepository<Candidatura, UUID> {
    List<Candidatura> findByMesaId(UUID mesaId);
    List<Candidatura> findByUsuarioId(UUID usuarioId);
    List<Candidatura> findByUsuarioIdAndStatus(UUID usuarioId, StatusCandidatura status);
    Optional<Candidatura> findByMesaIdAndUsuarioId(UUID mesaId, UUID usuarioId);
    long countByMesaIdAndStatus(UUID mesaId, StatusCandidatura status);
}
