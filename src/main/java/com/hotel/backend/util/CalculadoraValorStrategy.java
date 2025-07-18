package com.hotel.backend.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Strategy Pattern para cálculo de valores de hospedagem.
 * Permite diferentes estratégias de cálculo baseadas em regras de negócio.
 * Aplicando princípios de Clean Code e SOLID (Open/Closed Principle).
 */
public interface CalculadoraValorStrategy {

    /**
     * Calcula o valor da hospedagem baseado nos parâmetros fornecidos.
     *
     * @param dataEntrada Data de entrada no hotel
     * @param dataSaida Data de saída do hotel
     * @param adicionalVeiculo Se possui adicional de veículo
     * @return Valor total calculado
     */
    BigDecimal calcularValor(LocalDateTime dataEntrada, LocalDateTime dataSaida, boolean adicionalVeiculo);

    /**
     * Verifica se a estratégia é aplicável para o período informado.
     *
     * @param dataEntrada Data de entrada
     * @param dataSaida Data de saída
     * @return true se a estratégia é aplicável
     */
    boolean isAplicavel(LocalDateTime dataEntrada, LocalDateTime dataSaida);
}

