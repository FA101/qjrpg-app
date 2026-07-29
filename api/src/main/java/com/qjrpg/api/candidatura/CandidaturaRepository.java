package com.qjrpg.api.candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface CandidaturaRepository extends JpaRepository<Candidatura, UUID> {
    List<Candidatura> findByMesaId(UUID mesaId);
    List<Candidatura> findByUsuarioIdAndStatus(UUID usuarioId, StatusCandidatura status);
    long countByMesaIdAndStatus(UUID mesaId, StatusCandidatura status);
}
