package com.hotel.backend.service;

import com.hotel.backend.dto.*;
import com.hotel.backend.entity.Checkin;
import com.hotel.backend.entity.Hospede;
import com.hotel.backend.exception.CheckinNaoEncontradoException;
import com.hotel.backend.exception.HospedeJaNoHotelException;
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
 * Testes unitários para CheckinService.
 * Cobertura completa das funcionalidades de check-in, checkout e consultas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CheckinService")
class CheckinServiceTest {

    @Mock
    private CheckinRepository checkinRepository;

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private HospedeService hospedeService;

    @InjectMocks
    private CheckinService checkinService;

    private CheckinRequestDTO checkinRequestDTO;
    private HospedeRequestDTO hospedeRequestDTO;
    private Hospede hospede;
    private Checkin checkin;
    private final Long HOSPEDE_ID = 1L;
    private final Long CHECKIN_ID = 1L;
    private final String DOCUMENTO = "12345678901";

    @BeforeEach
    void setUp() {
        hospedeRequestDTO = HospedeRequestDTO.builder()
                .nome("João Silva")
                .documento(DOCUMENTO)
                .telefone("11999887766")
                .build();

        checkinRequestDTO = CheckinRequestDTO.builder()
                .hospede(hospedeRequestDTO)
                .dataEntrada(LocalDateTime.of(2024, 7, 12, 14, 0))
                .dataSaida(null)
                .adicionalVeiculo(false)
                .build();

        hospede = Hospede.builder()
                .id(HOSPEDE_ID)
                .nome("João Silva")
                .documento(DOCUMENTO)
                .telefone("11999887766")
                .build();

        checkin = Checkin.builder()
                .id(CHECKIN_ID)
                .hospede(hospede)
                .dataEntrada(LocalDateTime.of(2024, 7, 12, 14, 0))
                .dataSaida(null)
                .adicionalVeiculo(false)
                .valorTotal(null)
                .build();
    }

    @Test
    @DisplayName("Deve realizar check-in com hóspede existente")
    void deveRealizarCheckinComHospedeExistente() {
        // Given
        when(hospedeRepository.findByDocumento(DOCUMENTO)).thenReturn(Optional.of(hospede));
        when(checkinRepository.hospedeTemCheckinAtivo(hospede)).thenReturn(false);
        when(checkinRepository.save(any(Checkin.class))).thenReturn(checkin);

        // When
        CheckinResponseDTO resultado = checkinService.realizarCheckin(checkinRequestDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getHospede().getNome()).isEqualTo("João Silva");
        assertThat(resultado.getDataEntrada()).isEqualTo(LocalDateTime.of(2024, 7, 12, 14, 0));
        assertThat(resultado.getAtivo()).isTrue();

        verify(hospedeRepository).findByDocumento(DOCUMENTO);
        verify(checkinRepository).hospedeTemCheckinAtivo(hospede);
        verify(checkinRepository).save(any(Checkin.class));
        verify(hospedeService, never()).criarHospede(any());
    }

    @Test
    @DisplayName("Deve realizar check-in criando novo hóspede")
    void deveRealizarCheckinCriandoNovoHospede() {
        // Given
        HospedeResponseDTO novoHospedeResponse = HospedeResponseDTO.builder()
                .id(HOSPEDE_ID)
                .nome("João Silva")
                .documento(DOCUMENTO)
                .telefone("11999887766")
                .build();

        when(hospedeRepository.findByDocumento(DOCUMENTO)).thenReturn(Optional.empty());
        when(hospedeService.criarHospede(hospedeRequestDTO)).thenReturn(novoHospedeResponse);
        when(hospedeRepository.findById(HOSPEDE_ID)).thenReturn(Optional.of(hospede));
        when(checkinRepository.hospedeTemCheckinAtivo(hospede)).thenReturn(false);
        when(checkinRepository.save(any(Checkin.class))).thenReturn(checkin);

        // When
        CheckinResponseDTO resultado = checkinService.realizarCheckin(checkinRequestDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getHospede().getNome()).isEqualTo("João Silva");

        verify(hospedeRepository).findByDocumento(DOCUMENTO);
        verify(hospedeService).criarHospede(hospedeRequestDTO);
        verify(hospedeRepository).findById(HOSPEDE_ID);
        verify(checkinRepository).save(any(Checkin.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar check-in com hóspede já no hotel")
    void deveLancarExcecaoAoTentarCheckinComHospedeJaNoHotel() {
        // Given
        when(hospedeRepository.findByDocumento(DOCUMENTO)).thenReturn(Optional.of(hospede));
        when(checkinRepository.hospedeTemCheckinAtivo(hospede)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> checkinService.realizarCheckin(checkinRequestDTO))
                .isInstanceOf(HospedeJaNoHotelException.class)
                .hasMessageContaining("João Silva");

        verify(hospedeRepository).findByDocumento(DOCUMENTO);
        verify(checkinRepository).hospedeTemCheckinAtivo(hospede);
        verify(checkinRepository, never()).save(any(Checkin.class));
    }

    @Test
    @DisplayName("Deve realizar checkout com sucesso")
    void deveRealizarCheckoutComSucesso() {
        // Given
        CheckoutRequestDTO checkoutRequest = CheckoutRequestDTO.builder()
                .dataSaida(LocalDateTime.of(2024, 7, 14, 10, 30))
                .build();

        Checkin checkinComCheckout = Checkin.builder()
                .id(CHECKIN_ID)
                .hospede(hospede)
                .dataEntrada(LocalDateTime.of(2024, 7, 12, 14, 0))
                .dataSaida(LocalDateTime.of(2024, 7, 14, 10, 30))
                .adicionalVeiculo(false)
                .valorTotal(new BigDecimal("240.00"))
                .build();

        when(checkinRepository.findById(CHECKIN_ID)).thenReturn(Optional.of(checkin));
        when(checkinRepository.save(any(Checkin.class))).thenReturn(checkinComCheckout);

        // When
        CheckinResponseDTO resultado = checkinService.realizarCheckout(CHECKIN_ID, checkoutRequest);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getDataSaida()).isEqualTo(LocalDateTime.of(2024, 7, 14, 10, 30));
        assertThat(resultado.getValorTotal()).isEqualTo(new BigDecimal("240.00"));
        assertThat(resultado.getAtivo()).isFalse();

        verify(checkinRepository).findById(CHECKIN_ID);
        verify(checkinRepository).save(any(Checkin.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar checkout de check-in já finalizado")
    void deveLancarExcecaoAoTentarCheckoutDeCheckinJaFinalizado() {
        // Given
        checkin.setDataSaida(LocalDateTime.of(2024, 7, 13, 10, 0));
        CheckoutRequestDTO checkoutRequest = CheckoutRequestDTO.builder()
                .dataSaida(LocalDateTime.of(2024, 7, 14, 10, 30))
                .build();

        when(checkinRepository.findById(CHECKIN_ID)).thenReturn(Optional.of(checkin));

        // When & Then
        assertThatThrownBy(() -> checkinService.realizarCheckout(CHECKIN_ID, checkoutRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já foi finalizado");

        verify(checkinRepository).findById(CHECKIN_ID);
        verify(checkinRepository, never()).save(any(Checkin.class));
    }

    @Test
    @DisplayName("Deve buscar check-in por ID com sucesso")
    void deveBuscarCheckinPorIdComSucesso() {
        // Given
        when(checkinRepository.findById(CHECKIN_ID)).thenReturn(Optional.of(checkin));

        // When
        CheckinResponseDTO resultado = checkinService.buscarPorId(CHECKIN_ID);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(CHECKIN_ID);
        assertThat(resultado.getHospede().getNome()).isEqualTo("João Silva");

        verify(checkinRepository).findById(CHECKIN_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar check-in inexistente")
    void deveLancarExcecaoAoBuscarCheckinInexistente() {
        // Given
        when(checkinRepository.findById(CHECKIN_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> checkinService.buscarPorId(CHECKIN_ID))
                .isInstanceOf(CheckinNaoEncontradoException.class)
                .hasMessageContaining(CHECKIN_ID.toString());

        verify(checkinRepository).findById(CHECKIN_ID);
    }

    @Test
    @DisplayName("Deve listar todos os check-ins")
    void deveListarTodosOsCheckins() {
        // Given
        Checkin checkin2 = Checkin.builder()
                .id(2L)
                .hospede(hospede)
                .dataEntrada(LocalDateTime.of(2024, 7, 10, 15, 0))
                .dataSaida(LocalDateTime.of(2024, 7, 12, 11, 0))
                .adicionalVeiculo(true)
                .valorTotal(new BigDecimal("330.00"))
                .build();

        List<Checkin> checkins = Arrays.asList(checkin, checkin2);
        when(checkinRepository.buscarTodosComHospede()).thenReturn(checkins);

        // When
        List<CheckinResponseDTO> resultado = checkinService.listarTodos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(CHECKIN_ID);
        assertThat(resultado.get(1).getId()).isEqualTo(2L);
        assertThat(resultado.get(1).getValorTotal()).isEqualTo(new BigDecimal("330.00"));

        verify(checkinRepository).buscarTodosComHospede();
    }

    @Test
    @DisplayName("Deve listar check-ins ativos")
    void deveListarCheckinsAtivos() {
        // Given
        List<Checkin> checkinsAtivos = Arrays.asList(checkin);
        when(checkinRepository.buscarCheckinsAtivos()).thenReturn(checkinsAtivos);

        // When
        List<CheckinResponseDTO> resultado = checkinService.listarCheckinsAtivos();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAtivo()).isTrue();

        verify(checkinRepository).buscarCheckinsAtivos();
    }

    @Test
    @DisplayName("Deve listar check-ins finalizados")
    void deveListarCheckinsFinalizados() {
        // Given
        Checkin checkinFinalizado = Checkin.builder()
                .id(2L)
                .hospede(hospede)
                .dataEntrada(LocalDateTime.of(2024, 7, 10, 15, 0))
                .dataSaida(LocalDateTime.of(2024, 7, 12, 11, 0))
                .adicionalVeiculo(false)
                .valorTotal(new BigDecimal("240.00"))
                .build();

        List<Checkin> checkinsFinalizados = Arrays.asList(checkinFinalizado);
        when(checkinRepository.buscarCheckinsFinalizados()).thenReturn(checkinsFinalizados);

        // When
        List<CheckinResponseDTO> resultado = checkinService.listarCheckinsFinalizados();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getAtivo()).isFalse();
        assertThat(resultado.get(0).getValorTotal()).isEqualTo(new BigDecimal("240.00"));

        verify(checkinRepository).buscarCheckinsFinalizados();
    }

    @Test
    @DisplayName("Deve buscar check-ins por hóspede")
    void deveBuscarCheckinsPorHospede() {
        // Given
        List<Checkin> checkins = Arrays.asList(checkin);
        when(hospedeRepository.findById(HOSPEDE_ID)).thenReturn(Optional.of(hospede));
        when(checkinRepository.findByHospedeOrderByDataEntradaDesc(hospede)).thenReturn(checkins);

        // When
        List<CheckinResponseDTO> resultado = checkinService.buscarCheckinsPorHospede(HOSPEDE_ID);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getHospede().getId()).isEqualTo(HOSPEDE_ID);

        verify(hospedeRepository).findById(HOSPEDE_ID);
        verify(checkinRepository).findByHospedeOrderByDataEntradaDesc(hospede);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar check-ins de hóspede inexistente")
    void deveLancarExcecaoAoBuscarCheckinsDeHospedeInexistente() {
        // Given
        when(hospedeRepository.findById(HOSPEDE_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> checkinService.buscarCheckinsPorHospede(HOSPEDE_ID))
                .isInstanceOf(HospedeNaoEncontradoException.class)
                .hasMessageContaining(HOSPEDE_ID.toString());

        verify(hospedeRepository).findById(HOSPEDE_ID);
        verify(checkinRepository, never()).findByHospedeOrderByDataEntradaDesc(any());
    }

    @Test
    @DisplayName("Deve atualizar check-in ativo")
    void deveAtualizarCheckinAtivo() {
        // Given
        CheckinRequestDTO requestAtualizado = CheckinRequestDTO.builder()
                .hospede(hospedeRequestDTO)
                .dataEntrada(LocalDateTime.of(2024, 7, 12, 15, 0)) // Hora diferente
                .dataSaida(null)
                .adicionalVeiculo(true) // Mudou para true
                .build();

        when(checkinRepository.findById(CHECKIN_ID)).thenReturn(Optional.of(checkin));
        when(checkinRepository.save(any(Checkin.class))).thenReturn(checkin);

        // When
        CheckinResponseDTO resultado = checkinService.atualizarCheckin(CHECKIN_ID, requestAtualizado);

        // Then
        assertThat(resultado).isNotNull();
        verify(checkinRepository).findById(CHECKIN_ID);
        verify(checkinRepository).save(any(Checkin.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar check-in finalizado")
    void deveLancarExcecaoAoTentarAtualizarCheckinFinalizado() {
        // Given
        checkin.setDataSaida(LocalDateTime.of(2024, 7, 13, 10, 0));
        CheckinRequestDTO requestAtualizado = CheckinRequestDTO.builder()
                .hospede(hospedeRequestDTO)
                .dataEntrada(LocalDateTime.of(2024, 7, 12, 15, 0))
                .dataSaida(null)
                .adicionalVeiculo(true)
                .build();

        when(checkinRepository.findById(CHECKIN_ID)).thenReturn(Optional.of(checkin));

        // When & Then
        assertThatThrownBy(() -> checkinService.atualizarCheckin(CHECKIN_ID, requestAtualizado))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já finalizado");

        verify(checkinRepository).findById(CHECKIN_ID);
        verify(checkinRepository, never()).save(any(Checkin.class));
    }

    @Test
    @DisplayName("Deve remover check-in com sucesso")
    void deveRemoverCheckinComSucesso() {
        // Given
        when(checkinRepository.findById(CHECKIN_ID)).thenReturn(Optional.of(checkin));

        // When
        checkinService.removerCheckin(CHECKIN_ID);

        // Then
        verify(checkinRepository).findById(CHECKIN_ID);
        verify(checkinRepository).delete(checkin);
    }
}

