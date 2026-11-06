package br.com.finatech.api.dto.login;

/**
 * ResponseLoginDTO
 */
public record ResponseLoginDTO(
    String userName,
    String token
) {

}
