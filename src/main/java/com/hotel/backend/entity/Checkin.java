package com.hotel.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Entidade que representa um check-in no hotel.
 * Contém as regras de negócio para cálculo de valores.
 * Aplicando princípios de Clean Code e Single Responsibility Principle.
 */
@Entity
@Table(name = "checkin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "hospede")
public class Checkin {

    // Constantes para regras de negócio - evitando magic numbers
    private static final BigDecimal DIARIA_SEMANA = new BigDecimal("120.00");
    private static final BigDecimal DIARIA_FINAL_SEMANA = new BigDecimal("150.00");
    private static final BigDecimal GARAGEM_SEMANA = new BigDecimal("15.00");
    private static final BigDecimal GARAGEM_FINAL_SEMANA = new BigDecimal("20.00");
    private static final LocalTime HORARIO_LIMITE_SAIDA = LocalTime.of(16, 30);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospede_id", nullable = false)
    private Hospede hospede;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "adicional_veiculo", nullable = false)
    @Builder.Default
    private Boolean adicionalVeiculo = false;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Construtor para criação de check-in com dados básicos.
     */
    public Checkin(Hospede hospede, LocalDateTime dataEntrada, Boolean adicionalVeiculo) {
        this.hospede = hospede;
        this.dataEntrada = dataEntrada;
        this.adicionalVeiculo = adicionalVeiculo != null ? adicionalVeiculo : false;
    }

    /**
     * Calcula o valor total da hospedagem baseado nas regras de negócio.
     * Método público para permitir recálculo quando necessário.
     */
    public BigDecimal calcularValorTotal() {
        if (dataEntrada == null) {
            return BigDecimal.ZERO;
        }

        LocalDateTime dataFinalCalculo = dataSaida != null ? dataSaida : LocalDateTime.now();
        
        // Verifica se precisa cobrar diária extra por saída após 16:30h
        boolean cobraDiariaExtra = verificaCobrancaDiariaExtra(dataFinalCalculo);
        
        // Calcula número de diárias
        long numeroDiarias = calcularNumeroDiarias(dataEntrada, dataFinalCalculo, cobraDiariaExtra);
        
        BigDecimal valorDiarias = BigDecimal.ZERO;
        BigDecimal valorGaragem = BigDecimal.ZERO;

        // Calcula valor dia por dia
        LocalDateTime dataAtual = dataEntrada.toLocalDate().atStartOfDay();
        for (int i = 0; i < numeroDiarias; i++) {
            LocalDateTime diaCalculo = dataAtual.plusDays(i);
            
            if (isFinalDeSemana(diaCalculo)) {
                valorDiarias = valorDiarias.add(DIARIA_FINAL_SEMANA);
                if (adicionalVeiculo) {
                    valorGaragem = valorGaragem.add(GARAGEM_FINAL_SEMANA);
                }
            } else {
                valorDiarias = valorDiarias.add(DIARIA_SEMANA);
                if (adicionalVeiculo) {
                    valorGaragem = valorGaragem.add(GARAGEM_SEMANA);
                }
            }
        }

        this.valorTotal = valorDiarias.add(valorGaragem);
        return this.valorTotal;
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

    /**
     * Realiza o check-out definindo a data de saída e calculando o valor total.
     */
    public void realizarCheckout(LocalDateTime dataSaida) {
        this.dataSaida = dataSaida;
        calcularValorTotal();
    }

    /**
     * Verifica se o check-in está ativo (hóspede ainda no hotel).
     */
    public boolean isAtivo() {
        return dataSaida == null;
    }
}

