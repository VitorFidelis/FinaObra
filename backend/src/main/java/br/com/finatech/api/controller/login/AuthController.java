package br.com.finatech.api.controller.login;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.finatech.api.dto.login.RequestLoginDTO;
import br.com.finatech.api.dto.login.ResponseLoginDTO;
import br.com.finatech.api.dto.register.RequestRegisterDTO;
import br.com.finatech.api.dto.register.ResponseRegisterDTO;
import br.com.finatech.application.services.login.LoginService;
import br.com.finatech.application.services.login.RegisterService;
import br.com.finatech.infrastructure.openApi.AuthControllerDoc;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDoc {

    private final LoginService authService;
    private final RegisterService registerService;

    public AuthController(LoginService authService, RegisterService registerService) {
        this.authService = authService;
        this.registerService = registerService;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<ResponseLoginDTO> login(
        @RequestBody(required = true) @Valid RequestLoginDTO dto
    ){
        return ResponseEntity.ok().body(authService.execute(dto));
    }

    @PostMapping("/register")
    @Override
    public ResponseEntity<ResponseRegisterDTO> register(
        @RequestBody @Valid RequestRegisterDTO dto
    ) {
        ResponseRegisterDTO response = this.registerService.execute(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

}
