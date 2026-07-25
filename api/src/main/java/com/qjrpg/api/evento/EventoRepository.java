package com.qjrpg.api.evento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Interface, nao classe concreta: o Service depende desta abstracao,
 * nao do Postgres/Hibernate por tras dela (Dependency Inversion).
 * Isso e o que permite mockar o acesso a dados nos testes unitarios.
 */
public interface EventoRepository extends JpaRepository<Evento, UUID> {
}
