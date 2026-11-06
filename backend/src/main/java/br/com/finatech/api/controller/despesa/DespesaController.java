package br.com.finatech.api.controller.despesa;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.finatech.api.dto.despesa.RequestDespesaDTO;
import br.com.finatech.api.dto.despesa.ResponseDespesaDTO;
import br.com.finatech.application.services.despesa.CreateDespesa;
import br.com.finatech.application.services.despesa.DeleteDespesa;
import br.com.finatech.application.services.despesa.GetAllDespesa;
import br.com.finatech.application.services.despesa.UpdateDespesa;
import br.com.finatech.infrastructure.openApi.DespesaControllerDoc;
import br.com.finatech.infrastructure.security.UserDetailsImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "api/v1/despesas")
public class DespesaController implements DespesaControllerDoc {

    private final CreateDespesa create;
    private final DeleteDespesa delete;
    private final GetAllDespesa getAll;
    private final UpdateDespesa update;

    public DespesaController(CreateDespesa create, DeleteDespesa delete, GetAllDespesa getAll, UpdateDespesa update) {
        this.create = create;
        this.delete = delete;
        this.getAll = getAll;
        this.update = update;
    }

    @PostMapping
    @Override
    public ResponseEntity<ResponseDespesaDTO> create(
            @RequestBody(required = true) @Valid RequestDespesaDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResponseDespesaDTO response = this.create.execute(dto, userDetails.getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<ResponseDespesaDTO>> listAll(
            Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(this.getAll.execute(userDetails.getId(), pageable));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ResponseDespesaDTO> update(
            @PathVariable(required = true) UUID id,
            @RequestBody(required = true) @Valid RequestDespesaDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(this.update.execute(id, dto, userDetails.getId()));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(
            @PathVariable(required = true) UUID id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        this.delete.execute(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
