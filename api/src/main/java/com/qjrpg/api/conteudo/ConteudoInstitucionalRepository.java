package com.qjrpg.api.conteudo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface ConteudoInstitucionalRepository extends JpaRepository<ConteudoInstitucional, UUID> {
    Optional<ConteudoInstitucional> findBySecao(String secao);
}
