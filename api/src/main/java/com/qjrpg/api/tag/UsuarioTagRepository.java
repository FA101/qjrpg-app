package com.qjrpg.api.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioTagRepository extends JpaRepository<UsuarioTag, UUID> {
    List<UsuarioTag> findByUsuarioId(UUID usuarioId);
    Optional<UsuarioTag> findByUsuarioIdAndTagId(UUID usuarioId, UUID tagId);
    boolean existsByUsuarioIdAndTagId(UUID usuarioId, UUID tagId);
    List<UsuarioTag> findByTagId(UUID tagId);
}
