package br.com.finatech.api.dto.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * RequestRegisterDTO
 */
public record RequestRegisterDTO(

    @NotBlank(message = "O campo NOME não pode ser vazio")
    String nome,

    @Email(message = "Insira um e-mail válido")
    @NotBlank(message = "O campo EMAIL não pode ser vazio")
    String email,

    @NotBlank(message = "O campo USERNAME não pode ser vazio")
    String username,

    @NotBlank(message = "O campo PASSWORD não pode ser vazio")
    String password
) {

}
