package br.com.finatech.api.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

/**
 * DashboardResumoResponseDto
 */
public record DashboardResumoResponseDTO(
    BigDecimal totalPlanejado,
    BigDecimal totalRealizado,
    BigDecimal saldoRestante,
    List<CategoriaProgressoResponseDTO> progressoCategorias
) {

}
