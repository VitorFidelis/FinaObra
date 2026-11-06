package br.com.finatech.application.mapper;

import org.springframework.stereotype.Component;

import br.com.finatech.api.dto.login.RequestLoginDTO;
import br.com.finatech.api.dto.login.ResponseLoginDTO;
import br.com.finatech.domain.user.entity.User;

@Component
public class LoginMapper {

    public static User toEntity(RequestLoginDTO dto) {
        return User.builder()
        .userName(dto.userName())
        .password(dto.password())
        .build();
    }

    public static ResponseLoginDTO toDto(User entity, String token) {
        return new ResponseLoginDTO(
            entity.getUserName(),
            token
        );
    }
}
