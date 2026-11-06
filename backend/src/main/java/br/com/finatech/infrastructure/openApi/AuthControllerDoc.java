package br.com.finatech.infrastructure.openApi;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import br.com.finatech.api.dto.login.RequestLoginDTO;
import br.com.finatech.api.dto.login.ResponseLoginDTO;
import br.com.finatech.api.dto.register.RequestRegisterDTO;
import br.com.finatech.api.dto.register.ResponseRegisterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "Endpoint login e registro de usuarios.")
public interface AuthControllerDoc {

    @Operation(summary = "Faz login.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login feito com sucesso."),
        @ApiResponse(responseCode = "400", description = "Erro de validação nos campos enviados.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
        @ApiResponse(responseCode = "500", description = "Erro Interno do Servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
    })
    public ResponseEntity<ResponseLoginDTO> login(RequestLoginDTO dto);

    
    @Operation(summary = "Registra um usuario para login.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usurio registrado com sucesso."),
        @ApiResponse(responseCode = "404", description = "Recurso Não Encontrado.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
        @ApiResponse(responseCode = "400", description = "Erro de validação nos campos enviados.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
        @ApiResponse(responseCode = "500", description = "Erro Interno do Servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
    })
    public ResponseEntity<ResponseRegisterDTO> register(RequestRegisterDTO dto);
}
