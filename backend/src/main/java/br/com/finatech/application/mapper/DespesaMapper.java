package br.com.finatech.application.mapper;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import br.com.finatech.api.dto.despesa.RequestDespesaDTO;
import br.com.finatech.api.dto.despesa.ResponseDespesaDTO;
import br.com.finatech.domain.despesa.entity.Despesa;

@Component
public class DespesaMapper {

    public static Despesa toEntity(RequestDespesaDTO dto, UUID userId) {
        return Despesa.builder()
        .descricao(dto.descricao())
        .idCategoria(dto.idCategoria())
        .fornecedor(dto.fornecedor())
        .valor(dto.valor())
        .data(dto.data())
        .userId(userId)
        .build();
    }

    public static ResponseDespesaDTO toDto(Despesa entity, String username) {
        return new ResponseDespesaDTO(
            entity.getId(), 
            entity.getDescricao(), 
            entity.getIdCategoria(), 
            username,
            entity.getFornecedor(), 
            entity.getValor(), 
            entity.getData()
        );
    }

    public static Page<ResponseDespesaDTO> toDtoPage(Page<Despesa> pageEntity, String username) {
        return pageEntity.map(despesa -> DespesaMapper.toDto(despesa, username));
    }
}
