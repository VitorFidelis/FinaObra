package br.com.finatech.application.services.categoria;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.finatech.application.exception.NotFoundException;
import br.com.finatech.application.validantios.categoria.delete.ValidatorDeletedCategoria;
import br.com.finatech.domain.categoria.repository.CategoriaRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCategorias {

    private final CategoriaRepository repository;
    private final List<ValidatorDeletedCategoria> validators;

    public DeleteCategorias(CategoriaRepository repository, List<ValidatorDeletedCategoria> validators) {
        this.repository = repository;
        this.validators = validators;
    }

    @Transactional
    public void execute(Integer id, UUID userId) {
        validators.forEach(arg0 -> arg0.validar(id));
        this.repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Categoria com ID %d não encontrada para o usuário autenticado.", id)));
        this.repository.deleteById(id);
    }
}
