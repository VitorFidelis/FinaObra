package br.com.finatech.application.validantios.categoria.delete;

import org.springframework.stereotype.Component;

import br.com.finatech.application.exception.NotFoundException;
import br.com.finatech.domain.categoria.repository.CategoriaRepository;

@Component
public class ValidatorExistenciaCategoria implements ValidatorDeletedCategoria {

    private final CategoriaRepository repository;
    
    public ValidatorExistenciaCategoria(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(Integer id) {
        if (id != null && !this.repository.existsById(id)) {
            throw new NotFoundException(String.format("Categoria com o ID: %d informado não encontrada!", id));
        }
    }

}
