package br.com.finatech.infrastructure.openApi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import br.com.finatech.api.dto.categoria.RequestCategoriaDTO;
import br.com.finatech.api.dto.categoria.ResponseCategoriaDTO;
import br.com.finatech.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Categorias", description = "Endpoint para gerenciamento de categorias.")
public interface CategoriaControllerDoc {

        @Operation(summary = "Cria uma categoria.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso."),
                @ApiResponse(responseCode = "400", description = "Erro de validação nos campos enviados.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
                @ApiResponse(responseCode = "500", description = "Erro Interno do Servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
        })
        ResponseEntity<ResponseCategoriaDTO> create(
                RequestCategoriaDTO dto,
                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
        );

        @Operation(summary = "Atualiza uma categoria existente.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso."),
                @ApiResponse(responseCode = "400", description = "Erro de validação nos campos enviados.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
                @ApiResponse(responseCode = "404", description = "Recurso Não Encontrado.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
                @ApiResponse(responseCode = "500", description = "Erro Interno do Servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
        })
        ResponseEntity<ResponseCategoriaDTO> update(
                Integer id,
                RequestCategoriaDTO dto,
                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
        );

        @Operation(summary = "Deleta uma categoria por ID.")
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso.", content = @Content),
                @ApiResponse(responseCode = "404", description = "Recurso Não Encontrado.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json")),
                @ApiResponse(responseCode = "500", description = "Erro Interno do Servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class), mediaType = "application/problem+json"))
        })
        ResponseEntity<Void> delete(
                Integer id,
                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
        );

        @Operation(summary = "Lista todas as categorias paginadamente.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso."),
                @ApiResponse(responseCode = "500", description = "Erro Interno do Servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class),mediaType = "application/problem+json"))
        })
        ResponseEntity<Page<ResponseCategoriaDTO>> listAll(
                Pageable pageable,
                @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
        );

}
