package com.hotel.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para busca de hóspedes por nome, documento ou telefone.
 * Usado nos endpoints de consulta para check-in.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Parâmetros para busca de hóspedes")
public class HospedeBuscaDTO {

    @Schema(description = "Nome do hóspede para busca (busca parcial)", example = "João")
    private String nome;

    @Schema(description = "Documento do hóspede para busca", example = "12345678901")
    private String documento;

    @Schema(description = "Telefone do hóspede para busca", example = "11999887766")
    private String telefone;
}

