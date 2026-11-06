package br.com.finatech.application.services.categoria;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.api.dto.categoria.ResponseCategoriaDTO;
import br.com.finatech.application.mapper.CategoriaMapper;
import br.com.finatech.domain.categoria.entity.Categoria;
import br.com.finatech.domain.categoria.repository.CategoriaRepository;
import br.com.finatech.domain.user.repository.UserRepository;

@Service
public class GetAllCategorias {

    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;

    public GetAllCategorias(CategoriaRepository repository, UserRepository userRepository) {
        this.categoriaRepository = repository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ResponseCategoriaDTO> execute(UUID userId, Pageable pageable) {
        String username = userRepository.findUsernameById(userId)
                .orElse("Usuário Desconhecido");

        Page<Categoria> paginaEntidades = categoriaRepository.findByUserId(userId, pageable);

        return CategoriaMapper.toDtoPage(paginaEntidades, username);
    }
}
