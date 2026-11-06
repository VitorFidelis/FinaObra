package br.com.finatech.application.services.despesa;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.application.exception.NotFoundException;
import br.com.finatech.application.validantios.despesa.delete.ValidatorDeletedDespesa;
import br.com.finatech.domain.despesa.repository.DespesaRepository;

@Service
public class DeleteDespesa {

    private final DespesaRepository repository;
    private final List<ValidatorDeletedDespesa> validators;

    public DeleteDespesa(DespesaRepository repository, List<ValidatorDeletedDespesa> validators) {
        this.repository = repository;
        this.validators = validators;
    }

    @Transactional
    public void execute(UUID id, UUID userId) {
        this.validators.forEach(arg0 -> arg0.validar(id));
        this.repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Despesa com ID %s não encontrada para o usuário autenticado.", id)));
        this.repository.deleteById(id);
    }
}
