package br.com.finatech.application.validantios.categoria.create;

import java.util.UUID;

import br.com.finatech.api.dto.categoria.RequestCategoriaDTO;

public interface ValidatorCreatedCategoria {
    void validar(RequestCategoriaDTO dto, UUID userId);
}
