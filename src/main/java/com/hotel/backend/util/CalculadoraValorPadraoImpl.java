package com.hotel.backend.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Implementação padrão da estratégia de cálculo de valores.
 * Implementa as regras de negócio específicas do hotel.
 * Aplicando Strategy Pattern e Single Responsibility Principle.
 */
@Component
public class CalculadoraValorPadraoImpl implements CalculadoraValorStrategy {

    // Constantes para regras de negócio - evitando magic numbers
    private static final BigDecimal DIARIA_SEMANA = new BigDecimal("120.00");
    private static final BigDecimal DIARIA_FINAL_SEMANA = new BigDecimal("150.00");
    private static final BigDecimal GARAGEM_SEMANA = new BigDecimal("15.00");
    private static final BigDecimal GARAGEM_FINAL_SEMANA = new BigDecimal("20.00");
    private static final LocalTime HORARIO_LIMITE_SAIDA = LocalTime.of(16, 30);

    @Override
    public BigDecimal calcularValor(LocalDateTime dataEntrada, LocalDateTime dataSaida, boolean adicionalVeiculo) {
        if (dataEntrada == null) {
            return BigDecimal.ZERO;
        }

        LocalDateTime dataFinalCalculo = dataSaida != null ? dataSaida : LocalDateTime.now();
        
        // Verifica se precisa cobrar diária extra por saída após 16:30h
        boolean cobraDiariaExtra = verificaCobrancaDiariaExtra(dataFinalCalculo);
        
        // Calcula número de diárias
        long numeroDiarias = calcularNumeroDiarias(dataEntrada, dataFinalCalculo, cobraDiariaExtra);
        
        return calcularValorPorDiarias(dataEntrada, numeroDiarias, adicionalVeiculo);
    }

    @Override
    public boolean isAplicavel(LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        // Esta implementação é sempre aplicável (estratégia padrão)
        return true;
    }

    /**
     * Calcula o valor total baseado no número de diárias.
     */
    private BigDecimal calcularValorPorDiarias(LocalDateTime dataEntrada, long numeroDiarias, boolean adicionalVeiculo) {
        BigDecimal valorTotal = BigDecimal.ZERO;

        // Calcula valor dia por dia
        LocalDateTime dataAtual = dataEntrada.toLocalDate().atStartOfDay();
        for (int i = 0; i < numeroDiarias; i++) {
            LocalDateTime diaCalculo = dataAtual.plusDays(i);
            valorTotal = valorTotal.add(calcularValorDiaria(diaCalculo, adicionalVeiculo));
        }

        return valorTotal;
    }

    /**
     * Calcula o valor de uma diária específica.
     */
    private BigDecimal calcularValorDiaria(LocalDateTime data, boolean adicionalVeiculo) {
        BigDecimal valorDiaria;
        BigDecimal valorGaragem = BigDecimal.ZERO;

        if (isFinalDeSemana(data)) {
            valorDiaria = DIARIA_FINAL_SEMANA;
            if (adicionalVeiculo) {
                valorGaragem = GARAGEM_FINAL_SEMANA;
            }
        } else {
            valorDiaria = DIARIA_SEMANA;
            if (adicionalVeiculo) {
                valorGaragem = GARAGEM_SEMANA;
            }
        }

        return valorDiaria.add(valorGaragem);
    }

    /**
     * Verifica se é final de semana (sábado ou domingo).
     */
    private boolean isFinalDeSemana(LocalDateTime data) {
        DayOfWeek diaSemana = data.getDayOfWeek();
        return diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY;
    }

    /**
     * Verifica se deve cobrar diária extra por saída após 16:30h.
     */
    private boolean verificaCobrancaDiariaExtra(LocalDateTime dataSaidaCalculo) {
        if (dataSaidaCalculo == null) {
            return false;
        }
        return dataSaidaCalculo.toLocalTime().isAfter(HORARIO_LIMITE_SAIDA);
    }

    /**
     * Calcula o número de diárias a serem cobradas.
     */
    private long calcularNumeroDiarias(LocalDateTime entrada, LocalDateTime saida, boolean cobraDiariaExtra) {
        long diasEntreDatas = ChronoUnit.DAYS.between(entrada.toLocalDate(), saida.toLocalDate());
        
        // Mínimo de 1 diária
        if (diasEntreDatas == 0) {
            diasEntreDatas = 1;
        }
        
        // Adiciona diária extra se saída após 16:30h
        if (cobraDiariaExtra) {
            diasEntreDatas++;
        }
        
        return diasEntreDatas;
    }
}

