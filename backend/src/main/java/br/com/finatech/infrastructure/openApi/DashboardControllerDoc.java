package br.com.finatech.infrastructure.openApi;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import br.com.finatech.api.dto.dashboard.DashboardResumoResponseDTO;
import br.com.finatech.infrastructure.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Dashboard", description = "Endpoint de resumos em dashboard")
public interface DashboardControllerDoc {

    @Operation(summary = "Retorna as informações para geração do Dashboard.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumo listado com sucesso."),
        @ApiResponse(responseCode = "500", description = "Erro Interno do Servidor.", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<DashboardResumoResponseDTO> obterResumo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );

}
