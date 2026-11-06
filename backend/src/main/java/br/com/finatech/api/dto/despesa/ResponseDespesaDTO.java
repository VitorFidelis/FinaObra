package br.com.finatech.api.dto.despesa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * ResponseDespesaDTO — data serializada em ISO 8601 (yyyy-MM-dd) para compatibilidade com o front-end.
 */
public record ResponseDespesaDTO(
    UUID id,
    String descricao,
    Integer idCategoria,
    String username,
    String fornecedor,
    BigDecimal valor,
    LocalDate data
) {
}
