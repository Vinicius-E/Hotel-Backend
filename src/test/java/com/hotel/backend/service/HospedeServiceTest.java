package com.hotel.backend.service;

import com.hotel.backend.dto.HospedeBuscaDTO;
import com.hotel.backend.dto.HospedeRequestDTO;
import com.hotel.backend.dto.HospedeResponseDTO;
import com.hotel.backend.entity.Hospede;
import com.hotel.backend.exception.DocumentoJaCadastradoException;
import com.hotel.backend.exception.HospedeNaoEncontradoException;
import com.hotel.backend.repository.CheckinRepository;
import com.hotel.backend.repository.HospedeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para HospedeService.
 * Aplicando princípios de Clean Code: testes legíveis, organizados e com nomes descritivos.
 * Cobertura completa dos cenários de sucesso e falha.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do HospedeService")
class HospedeServiceTest {

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private CheckinRepository checkinRepository;

    @InjectMocks
    private HospedeService hospedeService;

    private HospedeRequestDTO hospedeRequestDTO;
    private Hospede hospede;
    private final Long HOSPEDE_ID = 1L;
    private final String DOCUMENTO = "12345678901";

    @BeforeEach
    void setUp() {
        hospedeRequestDTO = HospedeRequestDTO.builder()
                .nome("João Silva")
                .documento(DOCUMENTO)
                .telefone("11999887766")
                .build();

        hospede = Hospede.builder()
                .id(HOSPEDE_ID)
                .nome("João Silva")
                .documento(DOCUMENTO)
                .telefone("11999887766")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar hóspede com sucesso")
    void deveCriarHospedeComSucesso() {
        // Given
        when(hospedeRepository.existsByDocumento(DOCUMENTO)).thenReturn(false);
        when(hospedeRepository.save(any(Hospede.class))).thenReturn(hospede);
        when(checkinRepository.calcularValorTotalGastoPorHospede(any(Hospede.class)))
                .thenReturn(BigDecimal.ZERO);
        when(checkinRepository.buscarValorUltimaHospedagem(any(Hospede.class)))
                .thenReturn(Optional.empty());
        when(checkinRepository.hospedeTemCheckinAtivo(any(Hospede.class)))
                .thenReturn(false);

        // When
        HospedeResponseDTO resultado = hospedeService.criarHospede(hospedeRequestDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getDocumento()).isEqualTo(DOCUMENTO);
        assertThat(resultado.getTelefone()).isEqualTo("11999887766");
        assertThat(resultado.getValorTotalGasto()).isEqualTo(BigDecimal.ZERO);
        assertThat(resultado.getEstaNoHotel()).isFalse();

        verify(hospedeRepository).existsByDocumento(DOCUMENTO);
        verify(hospedeRepository).save(any(Hospede.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar hóspede com documento já cadastrado")
    void deveLancarExcecaoAoTentarCriarHospedeComDocumentoJaCadastrado() {
        // Given
        when(hospedeRepository.existsByDocumento(DOCUMENTO)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> hospedeService.criarHospede(hospedeRequestDTO))
                .isInstanceOf(DocumentoJaCadastradoException.class)
                .hasMessageContaining(DOCUMENTO);

        verify(hospedeRepository).existsByDocumento(DOCUMENTO);
        verify(hospedeRepository, never()).save(any(Hospede.class));
    }

    @Test
    @DisplayName("Deve buscar hóspede por ID com sucesso")
    void deveBuscarHospedePorIdComSucesso() {
        // Given
        when(hospedeRepository.findById(HOSPEDE_ID)).thenReturn(Optional.of(hospede));
        when(checkinRepository.calcularValorTotalGastoPorHospede(hospede))
                .thenReturn(new BigDecimal("450.00"));
        when(checkinRepository.buscarValorUltimaHospedagem(hospede))
                .thenReturn(Optional.of(new BigDecimal("240.00")));
        when(checkinRepository.hospedeTemCheckinAtivo(hospede))
                .thenReturn(true);

        // When
        HospedeResponseDTO resultado = hospedeService.buscarPorId(HOSPEDE_ID);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(HOSPEDE_ID);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getValorTotalGasto()).isEqualTo(new BigDecimal("450.00"));
        assertThat(resultado.getValorUltimaHospedagem()).isEqualTo(new BigDecimal("240.00"));
        assertThat(resultado.getEstaNoHotel()).isTrue();

        verify(hospedeRepository).findById(HOSPEDE_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar hóspede inexistente")
    void deveLancarExcecaoAoBuscarHospedeInexistente() {
        // Given
        when(hospedeRepository.findById(HOSPEDE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> hospedeService.buscarPorId(HOSPEDE_ID))
                .isInstanceOf(HospedeNaoEncontradoException.class)
                .hasMessageContaining(HOSPEDE_ID.toString());

        verify(hospedeRepository).findById(HOSPEDE_ID);
    }

    @Test
    @DisplayName("Deve listar todos os hóspedes")
    void deveListarTodosOsHospedes() {
        // Given
        Hospede hospede2 = Hospede.builder()
                .id(2L)
                .nome("Maria Santos")
                .documento("98765432109")
                .telefone("11888776655")
                .build();

        List<Hospede> hospedes = Arrays.asList(hospede, hospede2);
        when(hospedeRepository.buscarTodosComCheckins()).thenReturn(hospedes);
        when(checkinRepository.calcularValorTotalGastoPorHospede(any(Hospede.class)))
                .thenReturn(BigDecimal.ZERO);
        when(checkinRepository.buscarValorUltimaHospedagem(any(Hospede.class)))
                .thenReturn(Optional.empty());
        when(checkinRepository.hospedeTemCheckinAtivo(any(Hospede.class)))
                .thenReturn(false);

        // When
        List<HospedeResponseDTO> resultado = hospedeService.listarTodos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        assertThat(resultado.get(1).getNome()).isEqualTo("Maria Santos");

        verify(hospedeRepository).buscarTodosComCheckins();
    }

    @Test
    @DisplayName("Deve atualizar hóspede com sucesso")
    void deveAtualizarHospedeComSucesso() {
        // Given
        HospedeRequestDTO requestAtualizado = HospedeRequestDTO.builder()
                .nome("João Silva Santos")
                .documento(DOCUMENTO) // Mesmo documento
                .telefone("11999887799")
                .build();

        when(hospedeRepository.findById(HOSPEDE_ID)).thenReturn(Optional.of(hospede));
        when(hospedeRepository.save(any(Hospede.class))).thenReturn(hospede);
        when(checkinRepository.calcularValorTotalGastoPorHospede(any(Hospede.class)))
                .thenReturn(BigDecimal.ZERO);
        when(checkinRepository.buscarValorUltimaHospedagem(any(Hospede.class)))
                .thenReturn(Optional.empty());
        when(checkinRepository.hospedeTemCheckinAtivo(any(Hospede.class)))
                .thenReturn(false);

        // When
        HospedeResponseDTO resultado = hospedeService.atualizarHospede(HOSPEDE_ID, requestAtualizado);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva Santos");
        assertThat(resultado.getTelefone()).isEqualTo("11999887799");

        verify(hospedeRepository).findById(HOSPEDE_ID);
        verify(hospedeRepository).save(any(Hospede.class));
        verify(hospedeRepository, never()).existsByDocumento(anyString()); // Não verifica documento pois não mudou
    }

    @Test
    @DisplayName("Deve validar documento único ao atualizar com documento diferente")
    void deveValidarDocumentoUnicoAoAtualizarComDocumentoDiferente() {
        // Given
        String novoDocumento = "11111111111";
        HospedeRequestDTO requestAtualizado = HospedeRequestDTO.builder()
                .nome("João Silva Santos")
                .documento(novoDocumento)
                .telefone("11999887799")
                .build();

        when(hospedeRepository.findById(HOSPEDE_ID)).thenReturn(Optional.of(hospede));
        when(hospedeRepository.existsByDocumento(novoDocumento)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> hospedeService.atualizarHospede(HOSPEDE_ID, requestAtualizado))
                .isInstanceOf(DocumentoJaCadastradoException.class)
                .hasMessageContaining(novoDocumento);

        verify(hospedeRepository).findById(HOSPEDE_ID);
        verify(hospedeRepository).existsByDocumento(novoDocumento);
        verify(hospedeRepository, never()).save(any(Hospede.class));
    }

    @Test
    @DisplayName("Deve remover hóspede com sucesso")
    void deveRemoverHospedeComSucesso() {
        // Given
        when(hospedeRepository.findById(HOSPEDE_ID)).thenReturn(Optional.of(hospede));

        // When
        hospedeService.removerHospede(HOSPEDE_ID);

        // Then
        verify(hospedeRepository).findById(HOSPEDE_ID);
        verify(hospedeRepository).delete(hospede);
    }

    @Test
    @DisplayName("Deve buscar hóspedes com filtros")
    void deveBuscarHospedesComFiltros() {
        // Given
        HospedeBuscaDTO filtros = HospedeBuscaDTO.builder()
                .nome("João")
                .documento(null)
                .telefone(null)
                .build();

        List<Hospede> hospedes = Arrays.asList(hospede);
        when(hospedeRepository.buscarPorNomeDocumentoOuTelefone("João", null, null))
                .thenReturn(hospedes);
        when(checkinRepository.calcularValorTotalGastoPorHospede(any(Hospede.class)))
                .thenReturn(BigDecimal.ZERO);
        when(checkinRepository.buscarValorUltimaHospedagem(any(Hospede.class)))
                .thenReturn(Optional.empty());
        when(checkinRepository.hospedeTemCheckinAtivo(any(Hospede.class)))
                .thenReturn(false);

        // When
        List<HospedeResponseDTO> resultado = hospedeService.buscarHospedes(filtros);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");

        verify(hospedeRepository).buscarPorNomeDocumentoOuTelefone("João", null, null);
    }

    @Test
    @DisplayName("Deve buscar hóspedes que já saíram")
    void deveBuscarHospedesQueJaSairam() {
        // Given
        List<Hospede> hospedes = Arrays.asList(hospede);
        when(hospedeRepository.buscarHospedesQueJaSairam()).thenReturn(hospedes);
        when(checkinRepository.calcularValorTotalGastoPorHospede(any(Hospede.class)))
                .thenReturn(new BigDecimal("300.00"));
        when(checkinRepository.buscarValorUltimaHospedagem(any(Hospede.class)))
                .thenReturn(Optional.of(new BigDecimal("150.00")));
        when(checkinRepository.hospedeTemCheckinAtivo(any(Hospede.class)))
                .thenReturn(false);

        // When
        List<HospedeResponseDTO> resultado = hospedeService.buscarHospedesQueJaSairam();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getValorTotalGasto()).isEqualTo(new BigDecimal("300.00"));
        assertThat(resultado.get(0).getEstaNoHotel()).isFalse();

        verify(hospedeRepository).buscarHospedesQueJaSairam();
    }

    @Test
    @DisplayName("Deve buscar hóspedes no hotel")
    void deveBuscarHospedesNoHotel() {
        // Given
        List<Hospede> hospedes = Arrays.asList(hospede);
        when(hospedeRepository.buscarHospedesNoHotel()).thenReturn(hospedes);
        when(checkinRepository.calcularValorTotalGastoPorHospede(any(Hospede.class)))
                .thenReturn(BigDecimal.ZERO);
        when(checkinRepository.buscarValorUltimaHospedagem(any(Hospede.class)))
                .thenReturn(Optional.empty());
        when(checkinRepository.hospedeTemCheckinAtivo(any(Hospede.class)))
                .thenReturn(true);

        // When
        List<HospedeResponseDTO> resultado = hospedeService.buscarHospedesNoHotel();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstaNoHotel()).isTrue();

        verify(hospedeRepository).buscarHospedesNoHotel();
    }
}

