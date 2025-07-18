package com.hotel.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisições de criação e atualização de hóspede.
 * Aplicando princípios de Clean Code:
 * - Nome autoexplicativo
 * - Validações claras e específicas
 * - Documentação Swagger integrada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criação ou atualização de hóspede")
public class HospedeRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    @Schema(description = "Nome completo do hóspede", example = "João Silva", required = true)
    private String nome;

    @NotBlank(message = "Documento é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "Documento deve conter exatamente 11 dígitos")
    @Schema(description = "Documento do hóspede (CPF)", example = "12345678901", required = true)
    private String documento;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter entre 10 e 11 dígitos")
    @Schema(description = "Telefone do hóspede", example = "11999887766", required = true)
    private String telefone;
}

