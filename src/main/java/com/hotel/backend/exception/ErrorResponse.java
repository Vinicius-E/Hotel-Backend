package com.hotel.backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Classe para padronização de respostas de erro da API.
 * Aplicando princípios de Clean Code: estrutura consistente e informativa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Estrutura padrão para respostas de erro da API")
public class ErrorResponse {

    @Schema(description = "Timestamp do erro", example = "2024-07-12T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", example = "404")
    private Integer status;

    @Schema(description = "Tipo do erro", example = "Hóspede não encontrado")
    private String error;

    @Schema(description = "Mensagem detalhada do erro", example = "Hóspede não encontrado com ID: 1")
    private String message;

    @Schema(description = "Caminho da requisição que gerou o erro", example = "/api/hospedes/1")
    private String path;

    @Schema(description = "Detalhes adicionais do erro (opcional)")
    private Map<String, String> details;
}

