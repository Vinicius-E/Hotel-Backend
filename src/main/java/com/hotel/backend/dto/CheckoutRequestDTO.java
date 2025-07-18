package com.hotel.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para requisições de checkout.
 * Permite definir a data de saída para finalizar a hospedagem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para realização de checkout")
public class CheckoutRequestDTO {

    @NotNull(message = "Data de saída é obrigatória")
    @Schema(description = "Data e hora de saída do hotel", 
            example = "2024-07-14T10:30:00", 
            required = true)
    private LocalDateTime dataSaida;
}

