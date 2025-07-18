package com.hotel.backend.controller;

import com.hotel.backend.dto.HospedeBuscaDTO;
import com.hotel.backend.dto.HospedeRequestDTO;
import com.hotel.backend.dto.HospedeResponseDTO;
import com.hotel.backend.service.HospedeService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller REST para operações relacionadas a hóspedes.
 * Implementa CRUDL (Create, Read, Update, Delete, List) conforme requisitos funcionais.
 * Aplicando princípios de Clean Code e documentação Swagger completa.
 */
@RestController
@RequestMapping("/hospedes")
@Slf4j
@Tag(name = "Hóspedes", description = "Operações relacionadas ao cadastro e gestão de hóspedes")
public class HospedeController {

    @Autowired
    private HospedeService hospedeService;

    @Operation(
        summary = "Criar novo hóspede",
        description = "Cadastra um novo hóspede no sistema. O documento (CPF) deve ser único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Hóspede criado com sucesso",
                content = @Content(schema = @Schema(implementation = HospedeResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou documento já cadastrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<HospedeResponseDTO> criarHospede(
            @Valid @RequestBody HospedeRequestDTO request) {

        log.info("Recebida requisição para criar hóspede: {}", request.getNome());
        HospedeResponseDTO response = hospedeService.criarHospede(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Buscar hóspede por ID",
        description = "Retorna os dados de um hóspede específico incluindo valor total gasto e informações de hospedagem."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hóspede encontrado",
                content = @Content(schema = @Schema(implementation = HospedeResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Hóspede não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HospedeResponseDTO> buscarHospedePorId(
            @Parameter(description = "ID único do hóspede", example = "1")
            @PathVariable Long id) {

        log.info("Recebida requisição para buscar hóspede ID: {}", id);
        HospedeResponseDTO response = hospedeService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Listar todos os hóspedes",
        description = "Retorna lista completa de hóspedes cadastrados com informações de valor gasto e status de hospedagem."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de hóspedes retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<HospedeResponseDTO>> listarTodosHospedes() {
        log.info("Recebida requisição para listar todos os hóspedes");
        List<HospedeResponseDTO> response = hospedeService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Atualizar dados do hóspede",
        description = "Atualiza os dados de um hóspede existente. O documento (CPF) deve continuar único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hóspede atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = HospedeResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou documento já cadastrado"),
        @ApiResponse(responseCode = "404", description = "Hóspede não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<HospedeResponseDTO> atualizarHospede(
            @Parameter(description = "ID único do hóspede", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody HospedeRequestDTO request) {

        log.info("Recebida requisição para atualizar hóspede ID: {}", id);
        HospedeResponseDTO response = hospedeService.atualizarHospede(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Remover hóspede",
        description = "Remove um hóspede do sistema. Todos os check-ins relacionados também serão removidos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Hóspede removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Hóspede não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerHospede(
            @Parameter(description = "ID único do hóspede", example = "1")
            @PathVariable Long id) {

        log.info("Recebida requisição para remover hóspede ID: {}", id);
        hospedeService.removerHospede(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Buscar hóspedes para check-in",
        description = "Busca hóspedes por nome, documento ou telefone. Usado para localizar hóspedes durante o processo de check-in."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<HospedeResponseDTO>> buscarHospedes(
            @Parameter(description = "Nome do hóspede (busca parcial)", example = "João")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Documento do hóspede (CPF)", example = "12345678901")
            @RequestParam(required = false) String documento,
            @Parameter(description = "Telefone do hóspede", example = "11999887766")
            @RequestParam(required = false) String telefone) {

        log.info("Recebida requisição para buscar hóspedes com filtros - Nome: {}, Documento: {}, Telefone: {}",
                nome, documento, telefone);

        HospedeBuscaDTO filtros = HospedeBuscaDTO.builder()
                .nome(nome)
                .documento(documento)
                .telefone(telefone)
                .build();

        List<HospedeResponseDTO> response = hospedeService.buscarHospedes(filtros);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Consultar hóspedes que já saíram",
        description = "Retorna lista de hóspedes que já realizaram check-in e não estão mais no hotel, com valores gastos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/que-ja-sairam")
    public ResponseEntity<List<HospedeResponseDTO>> buscarHospedesQueJaSairam() {
        log.info("Recebida requisição para buscar hóspedes que já saíram");
        List<HospedeResponseDTO> response = hospedeService.buscarHospedesQueJaSairam();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Consultar hóspedes no hotel",
        description = "Retorna lista de hóspedes que ainda estão no hotel (com check-in ativo)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/no-hotel")
    public ResponseEntity<List<HospedeResponseDTO>> buscarHospedesNoHotel() {
        log.info("Recebida requisição para buscar hóspedes no hotel");
        List<HospedeResponseDTO> response = hospedeService.buscarHospedesNoHotel();
        return ResponseEntity.ok(response);
    }
}

