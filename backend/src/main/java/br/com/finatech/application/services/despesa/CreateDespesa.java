package br.com.finatech.application.services.despesa;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.api.dto.despesa.RequestDespesaDTO;
import br.com.finatech.api.dto.despesa.ResponseDespesaDTO;
import br.com.finatech.application.mapper.DespesaMapper;
import br.com.finatech.domain.despesa.entity.Despesa;
import br.com.finatech.domain.despesa.repository.DespesaRepository;
import br.com.finatech.domain.user.repository.UserRepository;

@Service
public class CreateDespesa {

    private final DespesaRepository repository;
    private final UserRepository userRepository; 

    public CreateDespesa(DespesaRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseDespesaDTO execute(RequestDespesaDTO dto, UUID userId) {
        Despesa saved = this.repository.save(DespesaMapper.toEntity(dto, userId));
        String username = this.userRepository.findUsernameById(userId).orElse("Usuário Desconhecido");
        return DespesaMapper.toDto(saved, username);
    }
}
