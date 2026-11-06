package br.com.finatech.api.dto.dashboard;

import java.math.BigDecimal;

public record CategoriaProgressoResponseDTO(
    String categoriaNome,
    BigDecimal valorPlanejado,
    BigDecimal valorGasto,
    Double percentualGasto
) {

}
