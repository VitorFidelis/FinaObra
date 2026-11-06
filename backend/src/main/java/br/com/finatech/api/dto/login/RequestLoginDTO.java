package br.com.finatech.api.dto.login;

import jakarta.validation.constraints.NotBlank;

/**
 * RequestLoginDTO
 */
public record RequestLoginDTO(

    @NotBlank(message = "O campo USERNAME não poder ser vazio")
    String userName,

    @NotBlank(message = "o campo PASSWORD não pode ser vazio")
    String password
) {

}
