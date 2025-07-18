package com.hotel.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a entidade Checkin.
 * Foca nas regras de negócio de cálculo de valores.
 */
@DisplayName("Testes da entidade Checkin")
class CheckinTest {

    private Hospede hospede;
    private Checkin checkin;

    @BeforeEach
    void setUp() {
        hospede = Hospede.builder()
                .id(1L)
                .nome("João Silva")
                .documento("12345678901")
                .telefone("11999887766")
                .build();
    }

    @Test
    @DisplayName("Deve calcular valor para 2 diárias de semana sem veículo")
    void deveCalcularValorPara2DiariasSemanaSemVeiculo() {
        // Given - Segunda a Quarta (2 diárias)
        LocalDateTime entrada = LocalDateTime.of(2024, 7, 15, 14, 0); // Segunda
        LocalDateTime saida = LocalDateTime.of(2024, 7, 17, 10, 0);   // Quarta

        checkin = Checkin.builder()
                .hospede(hospede)
                .dataEntrada(entrada)
                .dataSaida(saida)
                .adicionalVeiculo(false)
                .build();

        // When
        BigDecimal valor = checkin.calcularValorTotal();

        // Then
        // 2 diárias de semana: 2 * R$ 120,00 = R$ 240,00
        assertThat(valor).isEqualTo(new BigDecimal("240.00"));
    }

    @Test
    @DisplayName("Deve calcular valor para 2 diárias de semana com veículo")
    void deveCalcularValorPara2DiariasSemanaComVeiculo() {
        // Given - Segunda a Quarta (2 diárias) com veículo
        LocalDateTime entrada = LocalDateTime.of(2024, 7, 15, 14, 0); // Segunda
        LocalDateTime saida = LocalDateTime.of(2024, 7, 17, 10, 0);   // Quarta

        checkin = Checkin.builder()
                .hospede(hospede)
                .dataEntrada(entrada)
                .dataSaida(saida)
                .adicionalVeiculo(true)
                .build();

        // When
        BigDecimal valor = checkin.calcularValorTotal();

        // Then
        // 2 diárias de semana: 2 * R$ 120,00 = R$ 240,00
        // 2 diárias garagem semana: 2 * R$ 15,00 = R$ 30,00
        // Total: R$ 270,00
        assertThat(valor).isEqualTo(new BigDecimal("270.00"));
    }

    @Test
    @DisplayName("Deve calcular valor para final de semana sem veículo")
    void deveCalcularValorParaFinalDeSemanaSemVeiculo() {
        // Given - Sábado a Domingo (2 diárias de final de semana)
        LocalDateTime entrada = LocalDateTime.of(2024, 7, 13, 14, 0); // Sábado
        LocalDateTime saida = LocalDateTime.of(2024, 7, 15, 10, 0);   // Segunda

        checkin = Checkin.builder()
                .hospede(hospede)
                .dataEntrada(entrada)
                .dataSaida(saida)
                .adicionalVeiculo(false)
                .build();

        // When
        BigDecimal valor = checkin.calcularValorTotal();

        // Then
        // 2 diárias de final de semana: 2 * R$ 150,00 = R$ 300,00
        assertThat(valor).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Deve verificar se check-in está ativo")
    void deveVerificarSeCheckinEstaAtivo() {
        // Given
        checkin = Checkin.builder()
                .hospede(hospede)
                .dataEntrada(LocalDateTime.now())
                .dataSaida(null)
                .adicionalVeiculo(false)
                .build();

        // When & Then
        assertThat(checkin.isAtivo()).isTrue();

        // Quando define data de saída
        checkin.setDataSaida(LocalDateTime.now());
        assertThat(checkin.isAtivo()).isFalse();
    }
}

