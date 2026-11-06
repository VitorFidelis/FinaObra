package br.com.finatech.api.controller.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.finatech.api.dto.dashboard.DashboardResumoResponseDTO;
import br.com.finatech.application.services.dashboard.DashboardService;
import br.com.finatech.infrastructure.openApi.DashboardControllerDoc;
import br.com.finatech.infrastructure.security.UserDetailsImpl;

@RestController
@RequestMapping(value = "api/v1/dashboard")
public class DashboardController implements DashboardControllerDoc {

    private final DashboardService service;
    
    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/resumo")
    @Override
    public ResponseEntity<DashboardResumoResponseDTO> obterResumo(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(this.service.execute(userDetails.getId()));
    }
    
}
