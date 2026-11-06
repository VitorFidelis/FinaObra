package br.com.finatech.application.validantios.despesa.delete;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.finatech.application.exception.NotFoundException;
import br.com.finatech.domain.despesa.repository.DespesaRepository;

@Component
public class ValidatorExistenciaDespesa implements ValidatorDeletedDespesa {

    private final DespesaRepository repository;
    
    public ValidatorExistenciaDespesa(DespesaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(UUID id) {
        if (id != null && !this.repository.existsById(id)) {
            throw new NotFoundException(String.format("Despesa com o ID: %s informado não encontrada!", id));
        }
    }
}
