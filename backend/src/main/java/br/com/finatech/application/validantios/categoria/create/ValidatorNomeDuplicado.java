package br.com.finatech.application.validantios.categoria.create;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.finatech.api.dto.categoria.RequestCategoriaDTO;
import br.com.finatech.application.exception.AlreadyExistingException;
import br.com.finatech.domain.categoria.repository.CategoriaRepository;

@Component
public class ValidatorNomeDuplicado implements ValidatorCreatedCategoria {

    private final CategoriaRepository repository;

    public ValidatorNomeDuplicado(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(RequestCategoriaDTO dto, UUID userId) {
        if (dto != null && dto.nome() != null) {
            String nomeParaValidar = dto.nome().trim().toUpperCase();
            if (repository.existsByNomeAndUserId(nomeParaValidar, userId)) {
                throw new AlreadyExistingException("Já existe uma categoria cadastrada com este nome.");
            }
        }
    }
}
