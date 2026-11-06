package br.com.finatech.application.mapper;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import br.com.finatech.api.dto.categoria.RequestCategoriaDTO;
import br.com.finatech.api.dto.categoria.ResponseCategoriaDTO;
import br.com.finatech.domain.categoria.entity.Categoria;

@Component
public class CategoriaMapper {

    public static Categoria toEntity(RequestCategoriaDTO dto, UUID userId) {
        return Categoria.builder()
        .nome(dto.nome())
        .valorPlanejado(dto.valorPlanejado())
        .userId(userId)
        .build();
    }

    public static ResponseCategoriaDTO toDto(Categoria entity, String username) {
        return new ResponseCategoriaDTO(
            entity.getId(),
            entity.getNome(),
            username,
            entity.getValorPlanejado()
        );
    }

    public static Page<ResponseCategoriaDTO> toDtoPage(Page<Categoria> pageEntity, String username) {
        return pageEntity.map(categoria -> CategoriaMapper.toDto(categoria, username));
    }
}
