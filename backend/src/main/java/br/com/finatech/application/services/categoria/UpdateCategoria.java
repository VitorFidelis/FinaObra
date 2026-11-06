package br.com.finatech.application.services.categoria;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.finatech.api.dto.categoria.RequestCategoriaDTO;
import br.com.finatech.api.dto.categoria.ResponseCategoriaDTO;
import br.com.finatech.application.exception.AlreadyExistingException;
import br.com.finatech.application.exception.NotFoundException;
import br.com.finatech.application.mapper.CategoriaMapper;
import br.com.finatech.domain.categoria.entity.Categoria;
import br.com.finatech.domain.categoria.repository.CategoriaRepository;
import br.com.finatech.domain.user.repository.UserRepository;

@Service
public class UpdateCategoria {

    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;

    public UpdateCategoria(CategoriaRepository categoriaRepository, UserRepository userRepository) {
        this.categoriaRepository = categoriaRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseCategoriaDTO execute(Integer id, RequestCategoriaDTO dto, UUID userId) {
        Categoria categoria = this.categoriaRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Categoria com ID %d não encontrada para o usuário autenticado.", id)));

        String novoNome = dto.nome().trim().toUpperCase();
        if (!categoria.getNome().trim().equalsIgnoreCase(novoNome)) {
            if (this.categoriaRepository.existsByNomeAndUserId(novoNome, userId)) {
                throw new AlreadyExistingException("Já existe uma categoria cadastrada com este nome.");
            }
        }

        categoria.setNome(dto.nome().trim());
        categoria.setValorPlanejado(dto.valorPlanejado());

        Categoria saved = this.categoriaRepository.save(categoria);

        String username = this.userRepository.findUsernameById(userId)
                .orElse("Usuário Desconhecido");

        return CategoriaMapper.toDto(saved, username);
    }
}
