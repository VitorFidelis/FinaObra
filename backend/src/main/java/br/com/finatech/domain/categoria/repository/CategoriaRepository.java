package br.com.finatech.domain.categoria.repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.finatech.domain.categoria.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    Boolean existsByNomeAndUserId(String nome, UUID userId);

    Page<Categoria> findByUserId(UUID userId, Pageable pageable);

    Optional<Categoria> findByIdAndUserId(Integer id, UUID userId);

    @Query("SELECT COALESCE(SUM(c.valorPlanejado), 0) FROM Categoria c WHERE c.userId = :userId")
    BigDecimal somarTotalPlanejado(@Param("userId") UUID userId);
}
