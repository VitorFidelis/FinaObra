package br.com.finatech.application.services.dashboard;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.api.dto.dashboard.CategoriaProgressoResponseDTO;
import br.com.finatech.api.dto.dashboard.DashboardResumoResponseDTO;
import br.com.finatech.domain.categoria.repository.CategoriaRepository;
import br.com.finatech.domain.despesa.repository.DespesaRepository;

@Service
public class DashboardService {

    private final CategoriaRepository categoriaRepository;
    private final DespesaRepository despesaRepository;

    public DashboardService(CategoriaRepository categoriaRepository, DespesaRepository despesaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.despesaRepository = despesaRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResumoResponseDTO execute(UUID userId) {
        BigDecimal totalPlanejado = categoriaRepository.somarTotalPlanejado(userId);
        BigDecimal totalRealizado = despesaRepository.somarTotalGasto(userId);
        BigDecimal saldoRestante  = totalPlanejado.subtract(totalRealizado);

        List<CategoriaProgressoResponseDTO> progressoCategorias =
                despesaRepository.buscarProgressoCategorias(userId);

        return new DashboardResumoResponseDTO(
            totalPlanejado,
            totalRealizado,
            saldoRestante,
            progressoCategorias
        );
    }
}
