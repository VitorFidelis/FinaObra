package br.com.finatech.api.dto.categoria;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * RequestCategoriaDTO
 */
public record RequestCategoriaDTO(
    
    @NotBlank(message = "{validation.notblank}")
    String nome,

    @NotNull(message = "{validation.notnull}")
    @JsonProperty("valor_planejado")
    @DecimalMin(value = "2.00", message = "{validation.decimalmin}")
    @DecimalMax(value = "999999.99", message = "{validation.decimalmax}")
    @Digits(integer = 6, fraction = 2, message = "{validation.digits}")
    BigDecimal valorPlanejado
) {

}
