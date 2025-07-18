package com.hotel.backend.service;

import com.hotel.backend.dto.*;
import com.hotel.backend.entity.Checkin;
import com.hotel.backend.entity.Hospede;
import com.hotel.backend.exception.CheckinNaoEncontradoException;
import com.hotel.backend.exception.HospedeJaNoHotelException;
import com.hotel.backend.exception.HospedeNaoEncontradoException;
import com.hotel.backend.repository.CheckinRepository;
import com.hotel.backend.repository.HospedeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para operações relacionadas a check-ins.
 * Implementa as regras de negócio para hospedagem e cálculo de valores.
 * Aplicando Design Pattern: Strategy para diferentes tipos de cálculo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CheckinService {

    private final CheckinRepository checkinRepository;
    private final HospedeRepository hospedeRepository;
    private final HospedeService hospedeService;

    /**
     * Realiza check-in de um hóspede.
     * Pode criar novo hóspede ou usar existente.
     */
    public CheckinResponseDTO realizarCheckin(CheckinRequestDTO request) {
        log.info("Realizando check-in para hóspede: {}", request.getHospede().getNome());
        
        Hospede hospede = obterOuCriarHospede(request.getHospede());
        validarCheckinUnico(hospede);
        
        Checkin checkin = Checkin.builder()
                .hospede(hospede)
                .dataEntrada(request.getDataEntrada())
                .dataSaida(request.getDataSaida())
                .adicionalVeiculo(request.getAdicionalVeiculo())
                .build();
        
        // Se data de saída foi informada, calcula valor total
        if (request.getDataSaida() != null) {
            checkin.realizarCheckout(request.getDataSaida());
        }
        
        Checkin checkinSalvo = checkinRepository.save(checkin);
        log.info("Check-in realizado com sucesso. ID: {}", checkinSalvo.getId());
        
        return converterParaResponseDTO(checkinSalvo);
    }

    /**
     * Realiza checkout de um hóspede.
     */
    public CheckinResponseDTO realizarCheckout(Long checkinId, CheckoutRequestDTO request) {
        log.info("Realizando checkout para check-in ID: {}", checkinId);
        
        Checkin checkin = buscarCheckinPorId(checkinId);
        
        if (checkin.getDataSaida() != null) {
            throw new IllegalStateException("Check-in já foi finalizado");
        }
        
        checkin.realizarCheckout(request.getDataSaida());
        Checkin checkinAtualizado = checkinRepository.save(checkin);
        
        log.info("Checkout realizado com sucesso. Valor total: {}", checkinAtualizado.getValorTotal());
        
        return converterParaResponseDTO(checkinAtualizado);
    }

    /**
     * Busca check-in por ID.
     */
    @Transactional(readOnly = true)
    public CheckinResponseDTO buscarPorId(Long id) {
        log.info("Buscando check-in por ID: {}", id);
        
        Checkin checkin = buscarCheckinPorId(id);
        return converterParaResponseDTO(checkin);
    }

    /**
     * Lista todos os check-ins.
     */
    @Transactional(readOnly = true)
    public List<CheckinResponseDTO> listarTodos() {
        log.info("Listando todos os check-ins");
        
        List<Checkin> checkins = checkinRepository.buscarTodosComHospede();
        return checkins.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista check-ins ativos (hóspedes ainda no hotel).
     */
    @Transactional(readOnly = true)
    public List<CheckinResponseDTO> listarCheckinsAtivos() {
        log.info("Listando check-ins ativos");
        
        List<Checkin> checkins = checkinRepository.buscarCheckinsAtivos();
        return checkins.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista check-ins finalizados (hóspedes que já saíram).
     */
    @Transactional(readOnly = true)
    public List<CheckinResponseDTO> listarCheckinsFinalizados() {
        log.info("Listando check-ins finalizados");
        
        List<Checkin> checkins = checkinRepository.buscarCheckinsFinalizados();
        return checkins.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca check-ins de um hóspede específico.
     */
    @Transactional(readOnly = true)
    public List<CheckinResponseDTO> buscarCheckinsPorHospede(Long hospedeId) {
        log.info("Buscando check-ins do hóspede ID: {}", hospedeId);
        
        Hospede hospede = hospedeRepository.findById(hospedeId)
                .orElseThrow(() -> new HospedeNaoEncontradoException(hospedeId));
        
        List<Checkin> checkins = checkinRepository.findByHospedeOrderByDataEntradaDesc(hospede);
        return checkins.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza dados de um check-in (apenas se ainda não foi finalizado).
     */
    public CheckinResponseDTO atualizarCheckin(Long id, CheckinRequestDTO request) {
        log.info("Atualizando check-in ID: {}", id);
        
        Checkin checkin = buscarCheckinPorId(id);
        
        if (checkin.getDataSaida() != null) {
            throw new IllegalStateException("Não é possível atualizar check-in já finalizado");
        }
        
        checkin.setDataEntrada(request.getDataEntrada());
        checkin.setAdicionalVeiculo(request.getAdicionalVeiculo());
        
        // Se data de saída foi informada, realiza checkout
        if (request.getDataSaida() != null) {
            checkin.realizarCheckout(request.getDataSaida());
        }
        
        Checkin checkinAtualizado = checkinRepository.save(checkin);
        log.info("Check-in atualizado com sucesso. ID: {}", checkinAtualizado.getId());
        
        return converterParaResponseDTO(checkinAtualizado);
    }

    /**
     * Remove um check-in.
     */
    public void removerCheckin(Long id) {
        log.info("Removendo check-in ID: {}", id);
        
        Checkin checkin = buscarCheckinPorId(id);
        checkinRepository.delete(checkin);
        
        log.info("Check-in removido com sucesso. ID: {}", id);
    }

    // Métodos auxiliares privados

    private Checkin buscarCheckinPorId(Long id) {
        return checkinRepository.findById(id)
                .orElseThrow(() -> new CheckinNaoEncontradoException(id));
    }

    private Hospede obterOuCriarHospede(HospedeRequestDTO hospedeRequest) {
        // Tenta buscar hóspede existente por documento
        Optional<Hospede> hospedeExistente = hospedeRepository.findByDocumento(hospedeRequest.getDocumento());
        
        if (hospedeExistente.isPresent()) {
            log.info("Usando hóspede existente com documento: {}", hospedeRequest.getDocumento());
            return hospedeExistente.get();
        } else {
            log.info("Criando novo hóspede para check-in");
            HospedeResponseDTO novoHospede = hospedeService.criarHospede(hospedeRequest);
            return hospedeRepository.findById(novoHospede.getId())
                    .orElseThrow(() -> new HospedeNaoEncontradoException(novoHospede.getId()));
        }
    }

    private void validarCheckinUnico(Hospede hospede) {
        if (checkinRepository.hospedeTemCheckinAtivo(hospede)) {
            throw new HospedeJaNoHotelException(hospede.getNome());
        }
    }

    /**
     * Converte entidade para DTO de resposta.
     */
    private CheckinResponseDTO converterParaResponseDTO(Checkin checkin) {
        HospedeResponseDTO hospedeDTO = converterHospedeParaDTO(checkin.getHospede());
        
        return CheckinResponseDTO.builder()
                .id(checkin.getId())
                .hospede(hospedeDTO)
                .dataEntrada(checkin.getDataEntrada())
                .dataSaida(checkin.getDataSaida())
                .adicionalVeiculo(checkin.getAdicionalVeiculo())
                .valorTotal(checkin.getValorTotal())
                .ativo(checkin.isAtivo())
                .createdAt(checkin.getCreatedAt())
                .updatedAt(checkin.getUpdatedAt())
                .build();
    }

    private HospedeResponseDTO converterHospedeParaDTO(Hospede hospede) {
        return HospedeResponseDTO.builder()
                .id(hospede.getId())
                .nome(hospede.getNome())
                .documento(hospede.getDocumento())
                .telefone(hospede.getTelefone())
                .build();
    }
}

