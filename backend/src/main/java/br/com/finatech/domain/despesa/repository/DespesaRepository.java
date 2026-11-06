package br.com.finatech.domain.despesa.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.finatech.api.dto.dashboard.CategoriaProgressoResponseDTO;
import br.com.finatech.domain.despesa.entity.Despesa;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, UUID> {

    Page<Despesa> findByUserId(UUID userId, Pageable pageable);

    Optional<Despesa> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT COALESCE(SUM(d.valor), 0) FROM Despesa d WHERE d.userId = :userId")
    BigDecimal somarTotalGasto(@Param("userId") UUID userId);

    @Query("""
                SELECT new br.com.finatech.api.dto.dashboard.CategoriaProgressoResponseDTO(
                    c.nome,
                    c.valorPlanejado,
                    COALESCE(SUM(d.valor), 0),
                    CASE
                        WHEN c.valorPlanejado > 0
                        THEN CAST((COALESCE(SUM(d.valor), 0) * 100.0 / c.valorPlanejado) AS double)
                        ELSE 0.0
                    END
                )
                FROM Categoria c
                LEFT JOIN Despesa d ON d.idCategoria = c.id AND d.userId = :userId
                WHERE c.userId = :userId
                GROUP BY c.id, c.nome, c.valorPlanejado
            """)
    List<CategoriaProgressoResponseDTO> buscarProgressoCategorias(@Param("userId") UUID userId);
}
