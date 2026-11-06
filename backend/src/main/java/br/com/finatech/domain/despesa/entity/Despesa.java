package br.com.finatech.domain.despesa.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "despesas")
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "categoria_id")
    private Integer idCategoria;

    @Column(name = "fornecedor")
    private String fornecedor;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "data")
    private LocalDate data;

    /** UUID do usuário dono desta despesa. */
    @Column(name = "user_id")
    private UUID userId;

    public Despesa(UUID id, String descricao, Integer idCategoria, String fornecedor, BigDecimal valor,
            LocalDate data, UUID userId) {
        this.id = id;
        this.descricao = descricao;
        this.idCategoria = idCategoria;
        this.fornecedor = fornecedor;
        this.valor = valor;
        this.data = data;
        this.userId = userId;
    }
}
