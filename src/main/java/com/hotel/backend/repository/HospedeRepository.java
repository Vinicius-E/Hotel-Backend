package com.hotel.backend.repository;

import com.hotel.backend.entity.Hospede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de acesso a dados da entidade Hospede.
 * Implementa consultas customizadas para atender aos requisitos funcionais.
 * Aplicando princípios de Clean Code: nomes autoexplicativos e responsabilidade única.
 */
@Repository
public interface HospedeRepository extends JpaRepository<Hospede, Long> {

    /**
     * Busca hóspede por documento (CPF).
     * Usado para validar unicidade e busca específica.
     */
    Optional<Hospede> findByDocumento(String documento);

    /**
     * Verifica se existe hóspede com o documento informado.
     * Útil para validações de unicidade.
     */
    boolean existsByDocumento(String documento);

    /**
     * Busca hóspedes por nome, documento ou telefone.
     * Implementa a funcionalidade de busca para check-in.
     * Usa ILIKE para busca case-insensitive no PostgreSQL.
     */
    @Query("SELECT h FROM Hospede h WHERE " +
           "(:nome IS NULL OR LOWER(h.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:documento IS NULL OR h.documento = :documento) AND " +
           "(:telefone IS NULL OR h.telefone = :telefone)")
    List<Hospede> buscarPorNomeDocumentoOuTelefone(
            @Param("nome") String nome,
            @Param("documento") String documento,
            @Param("telefone") String telefone
    );

    /**
     * Busca hóspedes que já realizaram check-in e não estão mais no hotel.
     * Inclui informações de valor total gasto e valor da última hospedagem.
     */
    @Query("SELECT DISTINCT h FROM Hospede h " +
           "JOIN h.checkins c " +
           "WHERE c.dataSaida IS NOT NULL " +
           "ORDER BY h.nome")
    List<Hospede> buscarHospedesQueJaSairam();

    /**
     * Busca hóspedes que ainda estão no hotel (check-in ativo).
     */
    @Query("SELECT DISTINCT h FROM Hospede h " +
           "JOIN h.checkins c " +
           "WHERE c.dataSaida IS NULL " +
           "ORDER BY h.nome")
    List<Hospede> buscarHospedesNoHotel();

    /**
     * Busca hóspedes com informações de valor total gasto.
     * Usado para relatórios e consultas detalhadas.
     */
    @Query("SELECT h FROM Hospede h " +
           "LEFT JOIN FETCH h.checkins c " +
           "ORDER BY h.nome")
    List<Hospede> buscarTodosComCheckins();
}

