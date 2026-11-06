package br.com.finatech.api.dto.categoria;

import java.math.BigDecimal;

/**
 * ResponseCategoriaDTO
 */
public record ResponseCategoriaDTO(
    Integer id, 
    String nome,
    String username,
    BigDecimal valor_planejado
) {

}
