package br.com.finatech.api.controller.categoria;

import java.net.URI;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.finatech.api.dto.categoria.RequestCategoriaDTO;
import br.com.finatech.api.dto.categoria.ResponseCategoriaDTO;
import br.com.finatech.application.services.categoria.CreateCategoria;
import br.com.finatech.application.services.categoria.DeleteCategorias;
import br.com.finatech.application.services.categoria.GetAllCategorias;
import br.com.finatech.application.services.categoria.UpdateCategoria;
import br.com.finatech.infrastructure.openApi.CategoriaControllerDoc;
import br.com.finatech.infrastructure.security.UserDetailsImpl;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(value = "api/v1/categorias")
public class CategoriaController implements CategoriaControllerDoc {

    private final CreateCategoria create;
    private final GetAllCategorias getAll;
    private final DeleteCategorias delete;
    private final UpdateCategoria update;

    public CategoriaController(CreateCategoria create, GetAllCategorias getAll, DeleteCategorias delete,
            UpdateCategoria update) {
        this.create = create;
        this.getAll = getAll;
        this.delete = delete;
        this.update = update;
    }

    @PostMapping
    @Override
    public ResponseEntity<ResponseCategoriaDTO> create(
            @Valid @RequestBody(required = true) RequestCategoriaDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResponseCategoriaDTO response = this.create.execute(dto, userDetails.getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<Page<ResponseCategoriaDTO>> listAll(
            Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(this.getAll.execute(userDetails.getId(), pageable));
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ResponseCategoriaDTO> update(
            @PathVariable(required = true) Integer id,
            @Valid @RequestBody(required = true) RequestCategoriaDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(this.update.execute(id, dto, userDetails.getId()));
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(
            @PathVariable(required = true) Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        this.delete.execute(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

}
