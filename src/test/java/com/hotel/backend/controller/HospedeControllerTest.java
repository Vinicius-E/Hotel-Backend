package com.hotel.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.backend.dto.HospedeRequestDTO;
import com.hotel.backend.dto.HospedeResponseDTO;
import com.hotel.backend.service.HospedeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração para HospedeController.
 * Testa a camada web e integração com serviços.
 */
@WebMvcTest(HospedeController.class)
@DisplayName("Testes de integração do HospedeController")
class HospedeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private HospedeService hospedeService;

    @Test
    @DisplayName("Deve criar hóspede com sucesso")
    void deveCriarHospedeComSucesso() throws Exception {
        // Given
        HospedeRequestDTO request = HospedeRequestDTO.builder()
                .nome("João Silva")
                .documento("12345678901")
                .telefone("11999887766")
                .build();

        HospedeResponseDTO response = HospedeResponseDTO.builder()
                .id(1L)
                .nome("João Silva")
                .documento("12345678901")
                .telefone("11999887766")
                .valorTotalGasto(BigDecimal.ZERO)
                .valorUltimaHospedagem(BigDecimal.ZERO)
                .estaNoHotel(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(hospedeService.criarHospede(any(HospedeRequestDTO.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/hospedes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.documento").value("12345678901"))
                .andExpect(jsonPath("$.telefone").value("11999887766"));

        verify(hospedeService).criarHospede(any(HospedeRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 para dados inválidos")
    void deveRetornarErro400ParaDadosInvalidos() throws Exception {
        // Given
        HospedeRequestDTO request = HospedeRequestDTO.builder()
                .nome("") // Nome vazio - inválido
                .documento("123") // Documento inválido
                .telefone("123") // Telefone inválido
                .build();

        // When & Then
        mockMvc.perform(post("/hospedes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(hospedeService, never()).criarHospede(any());
    }

    @Test
    @DisplayName("Deve buscar hóspede por ID com sucesso")
    void deveBuscarHospedePorIdComSucesso() throws Exception {
        // Given
        Long hospedeId = 1L;
        HospedeResponseDTO response = HospedeResponseDTO.builder()
                .id(hospedeId)
                .nome("João Silva")
                .documento("12345678901")
                .telefone("11999887766")
                .valorTotalGasto(new BigDecimal("450.00"))
                .valorUltimaHospedagem(new BigDecimal("240.00"))
                .estaNoHotel(true)
                .build();

        when(hospedeService.buscarPorId(hospedeId)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/hospedes/{id}", hospedeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.valorTotalGasto").value(450.00))
                .andExpect(jsonPath("$.estaNoHotel").value(true));

        verify(hospedeService).buscarPorId(hospedeId);
    }

    @Test
    @DisplayName("Deve listar todos os hóspedes")
    void deveListarTodosOsHospedes() throws Exception {
        // Given
        HospedeResponseDTO hospede1 = HospedeResponseDTO.builder()
                .id(1L)
                .nome("João Silva")
                .documento("12345678901")
                .telefone("11999887766")
                .build();

        HospedeResponseDTO hospede2 = HospedeResponseDTO.builder()
                .id(2L)
                .nome("Maria Santos")
                .documento("98765432109")
                .telefone("11888776655")
                .build();

        List<HospedeResponseDTO> hospedes = Arrays.asList(hospede1, hospede2);
        when(hospedeService.listarTodos()).thenReturn(hospedes);

        // When & Then
        mockMvc.perform(get("/hospedes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[1].nome").value("Maria Santos"));

        verify(hospedeService).listarTodos();
    }

    @Test
    @DisplayName("Deve atualizar hóspede com sucesso")
    void deveAtualizarHospedeComSucesso() throws Exception {
        // Given
        Long hospedeId = 1L;
        HospedeRequestDTO request = HospedeRequestDTO.builder()
                .nome("João Silva Santos")
                .documento("12345678901")
                .telefone("11999887799")
                .build();

        HospedeResponseDTO response = HospedeResponseDTO.builder()
                .id(hospedeId)
                .nome("João Silva Santos")
                .documento("12345678901")
                .telefone("11999887799")
                .build();

        when(hospedeService.atualizarHospede(eq(hospedeId), any(HospedeRequestDTO.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(put("/hospedes/{id}", hospedeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Santos"))
                .andExpect(jsonPath("$.telefone").value("11999887799"));

        verify(hospedeService).atualizarHospede(eq(hospedeId), any(HospedeRequestDTO.class));
    }

    @Test
    @DisplayName("Deve remover hóspede com sucesso")
    void deveRemoverHospedeComSucesso() throws Exception {
        // Given
        Long hospedeId = 1L;
        doNothing().when(hospedeService).removerHospede(hospedeId);

        // When & Then
        mockMvc.perform(delete("/hospedes/{id}", hospedeId))
                .andExpect(status().isNoContent());

        verify(hospedeService).removerHospede(hospedeId);
    }

    @Test
    @DisplayName("Deve buscar hóspedes com filtros")
    void deveBuscarHospedesComFiltros() throws Exception {
        // Given
        HospedeResponseDTO hospede = HospedeResponseDTO.builder()
                .id(1L)
                .nome("João Silva")
                .documento("12345678901")
                .telefone("11999887766")
                .build();

        List<HospedeResponseDTO> hospedes = Arrays.asList(hospede);
        when(hospedeService.buscarHospedes(any())).thenReturn(hospedes);

        // When & Then
        mockMvc.perform(get("/hospedes/buscar")
                        .param("nome", "João")
                        .param("documento", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(hospedeService).buscarHospedes(any());
    }

    @Test
    @DisplayName("Deve buscar hóspedes que já saíram")
    void deveBuscarHospedesQueJaSairam() throws Exception {
        // Given
        HospedeResponseDTO hospede = HospedeResponseDTO.builder()
                .id(1L)
                .nome("João Silva")
                .estaNoHotel(false)
                .valorTotalGasto(new BigDecimal("450.00"))
                .build();

        List<HospedeResponseDTO> hospedes = Arrays.asList(hospede);
        when(hospedeService.buscarHospedesQueJaSairam()).thenReturn(hospedes);

        // When & Then
        mockMvc.perform(get("/hospedes/que-ja-sairam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estaNoHotel").value(false))
                .andExpect(jsonPath("$[0].valorTotalGasto").value(450.00));

        verify(hospedeService).buscarHospedesQueJaSairam();
    }

    @Test
    @DisplayName("Deve buscar hóspedes no hotel")
    void deveBuscarHospedesNoHotel() throws Exception {
        // Given
        HospedeResponseDTO hospede = HospedeResponseDTO.builder()
                .id(1L)
                .nome("João Silva")
                .estaNoHotel(true)
                .build();

        List<HospedeResponseDTO> hospedes = Arrays.asList(hospede);
        when(hospedeService.buscarHospedesNoHotel()).thenReturn(hospedes);

        // When & Then
        mockMvc.perform(get("/hospedes/no-hotel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estaNoHotel").value(true));

        verify(hospedeService).buscarHospedesNoHotel();
    }
}

