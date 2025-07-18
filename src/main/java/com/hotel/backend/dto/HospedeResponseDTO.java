package com.hotel.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de consultas de hóspede.
 * Contém informações calculadas como valor total gasto.
 * Aplicando princípio de separação entre API e domínio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de resposta do hóspede com informações calculadas")
public class HospedeResponseDTO {

    @Schema(description = "ID único do hóspede", example = "1")
    private Long id;

    @Schema(description = "Nome completo do hóspede", example = "João Silva")
    private String nome;

    @Schema(description = "Documento do hóspede (CPF)", example = "12345678901")
    private String documento;

    @Schema(description = "Telefone do hóspede", example = "11999887766")
    private String telefone;

    @Schema(description = "Valor total já gasto pelo hóspede no hotel", example = "450.00")
    private BigDecimal valorTotalGasto;

    @Schema(description = "Valor da última hospedagem", example = "240.00")
    private BigDecimal valorUltimaHospedagem;

    @Schema(description = "Indica se o hóspede está atualmente no hotel", example = "true")
    private Boolean estaNoHotel;

    @Schema(description = "Data de criação do registro", example = "2024-07-12T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2024-07-12T10:30:00")
    private LocalDateTime updatedAt;
}

