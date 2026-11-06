package br.com.finatech.api.dto.despesa;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * RequestDespesaDTO
 */
public record RequestDespesaDTO(
    @NotBlank(message = "{validation.notblank.descricao}")
    String descricao,

    @NotNull(message = "{validation.notblank.fornecedor}")
    @JsonProperty("categoria_id")
    Integer idCategoria,

    @NotBlank(message = "{validation.notnull.idCategoria}")
    String fornecedor,

    @NotNull(message = "{validation.notnull.valor}")
    @DecimalMin(value = "2.00", message = "{validation.decimalmin.valor}")
    @DecimalMax(value = "999999.99", message = "{validation.decimalmax.valor}")
    BigDecimal valor,

    @NotNull(message = "{validation.notnull.data}")
    LocalDate data
) {

}
