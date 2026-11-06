package br.com.finatech.domain.categoria.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "valor_planejado")
    private BigDecimal valorPlanejado;

    /** UUID do usuário dono desta categoria. */
    @Column(name = "user_id")
    private UUID userId;

    public Categoria(Integer id, String nome, BigDecimal valorPlanejado, UUID userId) {
        this.id = id;
        this.nome = nome;
        this.valorPlanejado = valorPlanejado;
        this.userId = userId;
    }
}
