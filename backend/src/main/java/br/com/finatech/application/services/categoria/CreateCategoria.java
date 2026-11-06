package br.com.finatech.application.services.categoria;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.finatech.api.dto.categoria.RequestCategoriaDTO;
import br.com.finatech.api.dto.categoria.ResponseCategoriaDTO;
import br.com.finatech.application.mapper.CategoriaMapper;
import br.com.finatech.application.validantios.categoria.create.ValidatorCreatedCategoria;
import br.com.finatech.domain.categoria.entity.Categoria;
import br.com.finatech.domain.categoria.repository.CategoriaRepository;
import br.com.finatech.domain.user.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCategoria {

    private final CategoriaRepository repository;
    private final List<ValidatorCreatedCategoria> validators;
    private final UserRepository userRepository;

    public CreateCategoria(CategoriaRepository categoriaRepository, List<ValidatorCreatedCategoria> validators,
            UserRepository userRepository) {
        this.repository = categoriaRepository;
        this.validators = validators;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseCategoriaDTO execute(RequestCategoriaDTO dto, UUID userId) {
        this.validators.forEach(validator -> validator.validar(dto, userId));
        Categoria entity = CategoriaMapper.toEntity(dto, userId);
        Categoria categoriaSalva = this.repository.save(entity);
        String username = this.userRepository.findUsernameById(userId)
                .orElse("Usuário Desconhecido");
        return CategoriaMapper.toDto(categoriaSalva, username);
    }
}
