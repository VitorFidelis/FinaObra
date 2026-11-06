package br.com.finatech.api.dto.register;

import java.util.UUID;

/**
 * ResponseRegisterDTO
 */
public record ResponseRegisterDTO(
    UUID id,
    String nome,
    String email,
    String username,
    String password
) {

}
