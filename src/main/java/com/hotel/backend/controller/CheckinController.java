package com.hotel.backend.controller;

import com.hotel.backend.dto.CheckinRequestDTO;
import com.hotel.backend.dto.CheckinResponseDTO;
import com.hotel.backend.dto.CheckoutRequestDTO;
import com.hotel.backend.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações relacionadas a check-ins.
 * Implementa funcionalidades de check-in, checkout e consultas conforme requisitos funcionais.
 * Aplicando princípios de Clean Code e documentação Swagger completa.
 */
@RestController
@RequestMapping("/checkins")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Check-ins", description = "Operações relacionadas ao check-in e checkout de hóspedes")
public class CheckinController {

    private final CheckinService checkinService;


    @Operation(
            summary = "Realizar check-in",
            description = "Realiza check-in de um hóspede. Pode criar novo hóspede ou usar existente baseado no documento. " +
                    "Calcula automaticamente o valor da hospedagem baseado nas regras de negócio."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Check-in realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CheckinResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou hóspede já possui check-in ativo"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<CheckinResponseDTO> realizarCheckin(
            @Valid @RequestBody CheckinRequestDTO request) {

        log.info("Recebida requisição para realizar check-in do hóspede: {}",
                request.getHospede().getNome());
        CheckinResponseDTO response = checkinService.realizarCheckin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Realizar checkout",
            description = "Finaliza a hospedagem definindo data de saída e calculando valor total baseado nas regras de negócio. " +
                    "Considera diárias de semana/fim de semana, adicional de veículo e diária extra após 16:30h."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CheckinResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Check-in já foi finalizado ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Check-in não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}/checkout")
    public ResponseEntity<CheckinResponseDTO> realizarCheckout(
            @Parameter(description = "ID único do check-in", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CheckoutRequestDTO request) {

        log.info("Recebida requisição para realizar checkout do check-in ID: {}", id);
        CheckinResponseDTO response = checkinService.realizarCheckout(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar check-in por ID",
            description = "Retorna os dados de um check-in específico incluindo informações do hóspede e valores calculados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check-in encontrado",
                    content = @Content(schema = @Schema(implementation = CheckinResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Check-in não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CheckinResponseDTO> buscarCheckinPorId(
            @Parameter(description = "ID único do check-in", example = "1")
            @PathVariable Long id) {

        log.info("Recebida requisição para buscar check-in ID: {}", id);
        CheckinResponseDTO response = checkinService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar todos os check-ins",
            description = "Retorna lista completa de check-ins ordenados por data de entrada (mais recente primeiro)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de check-ins retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<CheckinResponseDTO>> listarTodosCheckins() {
        log.info("Recebida requisição para listar todos os check-ins");
        List<CheckinResponseDTO> response = checkinService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar check-ins ativos",
            description = "Retorna lista de check-ins ativos (hóspedes que ainda estão no hotel)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de check-ins ativos retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/ativos")
    public ResponseEntity<List<CheckinResponseDTO>> listarCheckinsAtivos() {
        log.info("Recebida requisição para listar check-ins ativos");
        List<CheckinResponseDTO> response = checkinService.listarCheckinsAtivos();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar check-ins finalizados",
            description = "Retorna lista de check-ins finalizados (hóspedes que já saíram do hotel) com valores calculados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de check-ins finalizados retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/finalizados")
    public ResponseEntity<List<CheckinResponseDTO>> listarCheckinsFinalizados() {
        log.info("Recebida requisição para listar check-ins finalizados");
        List<CheckinResponseDTO> response = checkinService.listarCheckinsFinalizados();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar check-ins por hóspede",
            description = "Retorna histórico completo de check-ins de um hóspede específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico de check-ins retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/hospede/{hospedeId}")
    public ResponseEntity<List<CheckinResponseDTO>> buscarCheckinsPorHospede(
            @Parameter(description = "ID único do hóspede", example = "1")
            @PathVariable Long hospedeId) {

        log.info("Recebida requisição para buscar check-ins do hóspede ID: {}", hospedeId);
        List<CheckinResponseDTO> response = checkinService.buscarCheckinsPorHospede(hospedeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar check-in",
            description = "Atualiza dados de um check-in ativo. Não é possível atualizar check-ins já finalizados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check-in atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CheckinResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Check-in já finalizado ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Check-in não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CheckinResponseDTO> atualizarCheckin(
            @Parameter(description = "ID único do check-in", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CheckinRequestDTO request) {

        log.info("Recebida requisição para atualizar check-in ID: {}", id);
        CheckinResponseDTO response = checkinService.atualizarCheckin(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Remover check-in",
            description = "Remove um check-in do sistema. Operação irreversível."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Check-in removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Check-in não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCheckin(
            @Parameter(description = "ID único do check-in", example = "1")
            @PathVariable Long id) {

        log.info("Recebida requisição para remover check-in ID: {}", id);
        checkinService.removerCheckin(id);
        return ResponseEntity.noContent().build();
    }
}

