package br.com.finatech.application.services.login;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.api.dto.login.RequestLoginDTO;
import br.com.finatech.api.dto.login.ResponseLoginDTO;
import br.com.finatech.application.exception.NotFoundException;
import br.com.finatech.application.exception.PasswordInvalidException;
import br.com.finatech.application.mapper.LoginMapper;
import br.com.finatech.domain.user.entity.User;
import br.com.finatech.domain.user.repository.UserRepository;
import br.com.finatech.infrastructure.security.TokenService;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public ResponseLoginDTO execute(RequestLoginDTO dto) {
        // Busca o usuario pelo username informado
        User user = userRepository.findByUserName(
                dto.userName()).orElseThrow(() -> new NotFoundException("UserName não encontrado"));

        // svalida se as senhas são iguais
        if (passwordEncoder.matches(dto.password(), user.getPassword())) {
            // gerar o token com o usuario informado
            String token = this.tokenService.generateToken(user);

            // retorna o usuario e o token
            return LoginMapper.toDto(user, token);
        }else{
            throw new PasswordInvalidException("Senha Inválida");
        }
    }
}
