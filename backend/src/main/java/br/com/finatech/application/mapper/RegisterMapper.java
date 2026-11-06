package br.com.finatech.application.mapper;

import org.springframework.stereotype.Component;

import br.com.finatech.api.dto.register.RequestRegisterDTO;
import br.com.finatech.api.dto.register.ResponseRegisterDTO;
import br.com.finatech.domain.user.entity.User;

@Component
public class RegisterMapper {

    public static User toEntity(RequestRegisterDTO dto) {
        return User.builder()
        .nome(dto.nome())
        .email(dto.email())
        .userName(dto.username())
        .password(dto.password())
        .build();
    }

    public static ResponseRegisterDTO toDto(User entity) {
        return new ResponseRegisterDTO(
            entity.getId(),
            entity.getNome(),
            entity.getEmail(),
            entity.getUserName(),
            entity.getPassword()
        );
    }
}
