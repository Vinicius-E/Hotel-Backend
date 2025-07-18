package com.hotel.backend.repository;

import com.hotel.backend.entity.Checkin;
import com.hotel.backend.entity.Hospede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de acesso a dados da entidade Checkin.
 * Implementa consultas específicas para as regras de negócio do hotel.
 */
@Repository
public interface CheckinRepository extends JpaRepository<Checkin, Long> {

    /**
     * Busca todos os check-ins de um hóspede específico.
     * Ordenados por data de entrada (mais recente primeiro).
     */
    List<Checkin> findByHospedeOrderByDataEntradaDesc(Hospede hospede);

    /**
     * Busca check-ins ativos (hóspedes ainda no hotel).
     * Ordenados por data de entrada.
     */
    @Query("SELECT c FROM Checkin c " +
           "WHERE c.dataSaida IS NULL " +
           "ORDER BY c.dataEntrada")
    List<Checkin> buscarCheckinsAtivos();

    /**
     * Busca check-ins finalizados (hóspedes que já saíram).
     * Ordenados por data de saída (mais recente primeiro).
     */
    @Query("SELECT c FROM Checkin c " +
           "WHERE c.dataSaida IS NOT NULL " +
           "ORDER BY c.dataSaida DESC")
    List<Checkin> buscarCheckinsFinalizados();

    /**
     * Busca o check-in ativo de um hóspede específico.
     * Um hóspede pode ter apenas um check-in ativo por vez.
     */
    @Query("SELECT c FROM Checkin c " +
           "WHERE c.hospede = :hospede AND c.dataSaida IS NULL")
    Optional<Checkin> buscarCheckinAtivoDoHospede(@Param("hospede") Hospede hospede);

    /**
     * Verifica se um hóspede tem check-in ativo.
     * Usado para validações de negócio.
     */
    @Query("SELECT COUNT(c) > 0 FROM Checkin c " +
           "WHERE c.hospede = :hospede AND c.dataSaida IS NULL")
    boolean hospedeTemCheckinAtivo(@Param("hospede") Hospede hospede);

    /**
     * Calcula o valor total gasto por um hóspede.
     * Soma todos os valores de check-ins finalizados.
     */
    @Query("SELECT COALESCE(SUM(c.valorTotal), 0) FROM Checkin c " +
           "WHERE c.hospede = :hospede AND c.valorTotal IS NOT NULL")
    BigDecimal calcularValorTotalGastoPorHospede(@Param("hospede") Hospede hospede);

    /**
     * Busca o valor da última hospedagem de um hóspede.
     * Retorna o valor do check-in mais recente finalizado.
     */
    @Query("SELECT c.valorTotal FROM Checkin c " +
           "WHERE c.hospede = :hospede AND c.valorTotal IS NOT NULL " +
           "ORDER BY c.dataSaida DESC " +
           "LIMIT 1")
    Optional<BigDecimal> buscarValorUltimaHospedagem(@Param("hospede") Hospede hospede);

    /**
     * Busca todos os check-ins com informações do hóspede.
     * Usado para relatórios completos.
     */
    @Query("SELECT c FROM Checkin c " +
           "JOIN FETCH c.hospede h " +
           "ORDER BY c.dataEntrada DESC")
    List<Checkin> buscarTodosComHospede();
}

