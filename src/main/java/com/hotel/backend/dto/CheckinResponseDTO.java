package com.hotel.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de consultas de check-in.
 * Contém informações calculadas e dados do hóspede.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de resposta do check-in com informações calculadas")
public class CheckinResponseDTO {

    @Schema(description = "ID único do check-in", example = "1")
    private Long id;

    @Schema(description = "Dados do hóspede")
    private HospedeResponseDTO hospede;

    @Schema(description = "Data e hora de entrada no hotel", example = "2024-07-12T14:00:00")
    private LocalDateTime dataEntrada;

    @Schema(description = "Data e hora de saída do hotel", example = "2024-07-14T10:30:00")
    private LocalDateTime dataSaida;

    @Schema(description = "Indica se o hóspede utilizou vaga na garagem", example = "true")
    private Boolean adicionalVeiculo;

    @Schema(description = "Valor total da hospedagem", example = "450.00")
    private BigDecimal valorTotal;

    @Schema(description = "Indica se o check-in está ativo (hóspede ainda no hotel)", example = "false")
    private Boolean ativo;

    @Schema(description = "Data de criação do registro", example = "2024-07-12T14:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2024-07-14T10:30:00")
    private LocalDateTime updatedAt;
}

