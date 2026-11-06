package br.com.finatech.application.services.despesa;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.api.dto.despesa.RequestDespesaDTO;
import br.com.finatech.api.dto.despesa.ResponseDespesaDTO;
import br.com.finatech.application.exception.NotFoundException;
import br.com.finatech.application.mapper.DespesaMapper;
import br.com.finatech.domain.despesa.entity.Despesa;
import br.com.finatech.domain.despesa.repository.DespesaRepository;
import br.com.finatech.domain.user.repository.UserRepository;

@Service
public class UpdateDespesa {

    private final DespesaRepository despesaRepository;
    private final UserRepository userRepository;

    public UpdateDespesa(DespesaRepository despesaRepository, UserRepository userRepository) {
        this.despesaRepository = despesaRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseDespesaDTO execute(UUID id, RequestDespesaDTO dto, UUID userId) {
        Despesa despesa = this.despesaRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Despesa com ID %s não encontrada para o usuário autenticado.", id)));

        despesa.setDescricao(dto.descricao());
        despesa.setIdCategoria(dto.idCategoria());
        despesa.setFornecedor(dto.fornecedor());
        despesa.setValor(dto.valor());
        despesa.setData(dto.data());

        Despesa saved = this.despesaRepository.save(despesa);

        String username = this.userRepository.findUsernameById(userId)
                .orElse("Usuário Desconhecido");

        return DespesaMapper.toDto(saved, username);
    }
}
