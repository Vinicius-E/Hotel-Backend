package com.hotel.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para requisições de check-in.
 * Seguindo o padrão de exemplo fornecido no documento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para realização de check-in")
public class CheckinRequestDTO {

    @NotNull(message = "Dados do hóspede são obrigatórios")
    @Schema(description = "Dados do hóspede para check-in", required = true)
    private HospedeRequestDTO hospede;

    @NotNull(message = "Data de entrada é obrigatória")
    @Schema(description = "Data e hora de entrada no hotel", 
            example = "2024-07-12T14:00:00", 
            required = true)
    private LocalDateTime dataEntrada;

    @Schema(description = "Data e hora de saída do hotel (opcional para check-in)", 
            example = "2024-07-14T10:30:00")
    private LocalDateTime dataSaida;

    @Schema(description = "Indica se o hóspede precisa de vaga na garagem", 
            example = "true", 
            defaultValue = "false")
    @Builder.Default
    private Boolean adicionalVeiculo = false;
}

