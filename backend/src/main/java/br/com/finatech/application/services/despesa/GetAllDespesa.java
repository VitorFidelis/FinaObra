package br.com.finatech.application.services.despesa;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.api.dto.despesa.ResponseDespesaDTO;
import br.com.finatech.application.mapper.DespesaMapper;
import br.com.finatech.domain.despesa.entity.Despesa;
import br.com.finatech.domain.despesa.repository.DespesaRepository;
import br.com.finatech.domain.user.repository.UserRepository;

@Service
public class GetAllDespesa {

    private final DespesaRepository despesaRepository;
    private final UserRepository userRepository;

    public GetAllDespesa(DespesaRepository despesaRepository, UserRepository userRepository) {
        this.despesaRepository = despesaRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ResponseDespesaDTO> execute(UUID userId, Pageable pageable) {
        String username = this.userRepository.findUsernameById(userId)
                .orElse("Usuário Desconhecido");

        Page<Despesa> despesas = this.despesaRepository.findByUserId(userId, pageable);

        return DespesaMapper.toDtoPage(despesas, username);
    }
}
