package com.hotel.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um hóspede do hotel.
 * Aplicando princípios de Clean Code:
 * - Nome significativo e autoexplicativo
 * - Responsabilidade única: representar dados do hóspede
 * - Uso de Lombok para reduzir boilerplate
 */
@Entity
@Table(name = "hospede")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "checkins")
public class Hospede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "documento", nullable = false, unique = true, length = 50)
    private String documento;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @OneToMany(mappedBy = "hospede", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Checkin> checkins = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Construtor para criação de hóspede com dados básicos.
     * Seguindo o princípio de Clean Code: construtores específicos para casos de uso.
     */
    public Hospede(String nome, String documento, String telefone) {
        this.nome = nome;
        this.documento = documento;
        this.telefone = telefone;
        this.checkins = new ArrayList<>();
    }

    /**
     * Método de conveniência para adicionar um check-in.
     * Mantém a consistência bidirecional do relacionamento.
     */
    public void adicionarCheckin(Checkin checkin) {
        checkins.add(checkin);
        checkin.setHospede(this);
    }

    /**
     * Método de conveniência para remover um check-in.
     * Mantém a consistência bidirecional do relacionamento.
     */
    public void removerCheckin(Checkin checkin) {
        checkins.remove(checkin);
        checkin.setHospede(null);
    }
}

