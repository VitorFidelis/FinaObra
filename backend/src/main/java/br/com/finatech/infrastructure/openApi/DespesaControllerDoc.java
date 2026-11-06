package br.com.finatech.infrastructure.openApi;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import br.com.finatech.api.dto.despesa.RequestDespesaDTO;
import br.com.finatech.api.dto.despesa.ResponseDespesaDTO;
import br.com.finatech.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Despesas", description = "Endpoint para gerenciamento de despesas.")
public interface DespesaControllerDoc {

    @Operation(summary = "Cria uma despesas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Despesa criada com sucessso."),
            @ApiResponse(responseCode = "500", description = "Error interno no servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos enviados.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Recurso Não Encontrado.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
    })
    ResponseEntity<ResponseDespesaDTO> create(
            RequestDespesaDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "Lista todas as depesas paginadamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despesas listadas com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Recurso Não Encontrado.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
    })
    ResponseEntity<Page<ResponseDespesaDTO>> listAll(
            Pageable pageable,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "Atualiza uma despesa existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despesa atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos enviados.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Recurso Não Encontrado.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
    })
    ResponseEntity<ResponseDespesaDTO> update(
            UUID id,
            RequestDespesaDTO dto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "Deleta despesa por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Despesa deletada com sucesso.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso Não Encontrado.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
    })
    ResponseEntity<Void> delete(
            UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
