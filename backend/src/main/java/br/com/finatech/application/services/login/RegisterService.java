package br.com.finatech.application.services.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.api.dto.register.RequestRegisterDTO;
import br.com.finatech.api.dto.register.ResponseRegisterDTO;
import br.com.finatech.application.exception.AlreadyExistingException;
import br.com.finatech.application.mapper.RegisterMapper;
import br.com.finatech.domain.user.entity.User;
import br.com.finatech.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * RegisterService
 */
@RequiredArgsConstructor
@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseRegisterDTO execute(RequestRegisterDTO dto) {
        
        // valida se o username ja existe
        if (this.userRepository.existsByUserName(dto.username())) {
            throw new AlreadyExistingException("USERNAME indisponivel");
        }
        
        // converte de Dto para Entity
        User entity = RegisterMapper.toEntity(dto);

        // gera o hash de senha para salvar no banco
        entity.setPassword(passwordEncoder.encode(dto.password()));

        // salva no banco
        User newUser = this.userRepository.save(entity);

        // retorna as informações do novo usuario
        return RegisterMapper.toDto(newUser);
    }

}
