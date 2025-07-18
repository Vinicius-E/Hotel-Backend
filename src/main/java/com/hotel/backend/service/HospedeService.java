package com.hotel.backend.service;

import com.hotel.backend.dto.*;
import com.hotel.backend.entity.Hospede;
import com.hotel.backend.exception.DocumentoJaCadastradoException;
import com.hotel.backend.exception.HospedeNaoEncontradoException;
import com.hotel.backend.repository.CheckinRepository;
import com.hotel.backend.repository.HospedeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para operações relacionadas a hóspedes.
 * Implementa a lógica de negócio e aplica princípios de Clean Code.
 * Aplicando Design Pattern: Facade para simplificar operações complexas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HospedeService {

    private final HospedeRepository hospedeRepository;
    private final CheckinRepository checkinRepository;

    /**
     * Cria um novo hóspede.
     * Valida se o documento já não está cadastrado.
     */
    public HospedeResponseDTO criarHospede(HospedeRequestDTO request) {
        log.info("Criando novo hóspede com documento: {}", request.getDocumento());
        
        validarDocumentoUnico(request.getDocumento());
        
        Hospede hospede = Hospede.builder()
                .nome(request.getNome())
                .documento(request.getDocumento())
                .telefone(request.getTelefone())
                .build();
        
        Hospede hospedeSalvo = hospedeRepository.save(hospede);
        log.info("Hóspede criado com sucesso. ID: {}", hospedeSalvo.getId());
        
        return converterParaResponseDTO(hospedeSalvo);
    }

    /**
     * Busca hóspede por ID.
     */
    @Transactional(readOnly = true)
    public HospedeResponseDTO buscarPorId(Long id) {
        log.info("Buscando hóspede por ID: {}", id);
        
        Hospede hospede = buscarHospedePorId(id);
        return converterParaResponseDTO(hospede);
    }

    /**
     * Lista todos os hóspedes.
     */
    @Transactional(readOnly = true)
    public List<HospedeResponseDTO> listarTodos() {
        log.info("Listando todos os hóspedes");
        
        List<Hospede> hospedes = hospedeRepository.buscarTodosComCheckins();
        return hospedes.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza dados de um hóspede.
     */
    public HospedeResponseDTO atualizarHospede(Long id, HospedeRequestDTO request) {
        log.info("Atualizando hóspede ID: {}", id);
        
        Hospede hospede = buscarHospedePorId(id);
        
        // Valida documento único apenas se foi alterado
        if (!hospede.getDocumento().equals(request.getDocumento())) {
            validarDocumentoUnico(request.getDocumento());
        }
        
        hospede.setNome(request.getNome());
        hospede.setDocumento(request.getDocumento());
        hospede.setTelefone(request.getTelefone());
        
        Hospede hospedeAtualizado = hospedeRepository.save(hospede);
        log.info("Hóspede atualizado com sucesso. ID: {}", hospedeAtualizado.getId());
        
        return converterParaResponseDTO(hospedeAtualizado);
    }

    /**
     * Remove um hóspede.
     * Aplica cascade delete para os check-ins relacionados.
     */
    public void removerHospede(Long id) {
        log.info("Removendo hóspede ID: {}", id);
        
        Hospede hospede = buscarHospedePorId(id);
        hospedeRepository.delete(hospede);
        
        log.info("Hóspede removido com sucesso. ID: {}", id);
    }

    /**
     * Busca hóspedes por nome, documento ou telefone.
     * Implementa a funcionalidade de busca para check-in.
     */
    @Transactional(readOnly = true)
    public List<HospedeResponseDTO> buscarHospedes(HospedeBuscaDTO filtros) {
        log.info("Buscando hóspedes com filtros: {}", filtros);
        
        List<Hospede> hospedes = hospedeRepository.buscarPorNomeDocumentoOuTelefone(
                filtros.getNome(),
                filtros.getDocumento(),
                filtros.getTelefone()
        );
        
        return hospedes.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Consulta hóspedes que já realizaram check-in e não estão mais no hotel.
     */
    @Transactional(readOnly = true)
    public List<HospedeResponseDTO> buscarHospedesQueJaSairam() {
        log.info("Buscando hóspedes que já saíram do hotel");
        
        List<Hospede> hospedes = hospedeRepository.buscarHospedesQueJaSairam();
        return hospedes.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Consulta hóspedes que ainda estão no hotel.
     */
    @Transactional(readOnly = true)
    public List<HospedeResponseDTO> buscarHospedesNoHotel() {
        log.info("Buscando hóspedes que estão no hotel");
        
        List<Hospede> hospedes = hospedeRepository.buscarHospedesNoHotel();
        return hospedes.stream()
                .map(this::converterParaResponseDTO)
                .collect(Collectors.toList());
    }

    // Métodos auxiliares privados

    private Hospede buscarHospedePorId(Long id) {
        return hospedeRepository.findById(id)
                .orElseThrow(() -> new HospedeNaoEncontradoException(id));
    }

    private void validarDocumentoUnico(String documento) {
        if (hospedeRepository.existsByDocumento(documento)) {
            throw new DocumentoJaCadastradoException(documento);
        }
    }

    /**
     * Converte entidade para DTO de resposta.
     * Calcula valores agregados (valor total gasto, última hospedagem, etc.).
     */
    private HospedeResponseDTO converterParaResponseDTO(Hospede hospede) {
        BigDecimal valorTotalGasto = checkinRepository.calcularValorTotalGastoPorHospede(hospede);
        BigDecimal valorUltimaHospedagem = checkinRepository.buscarValorUltimaHospedagem(hospede)
                .orElse(BigDecimal.ZERO);
        boolean estaNoHotel = checkinRepository.hospedeTemCheckinAtivo(hospede);

        return HospedeResponseDTO.builder()
                .id(hospede.getId())
                .nome(hospede.getNome())
                .documento(hospede.getDocumento())
                .telefone(hospede.getTelefone())
                .valorTotalGasto(valorTotalGasto)
                .valorUltimaHospedagem(valorUltimaHospedagem)
                .estaNoHotel(estaNoHotel)
                .createdAt(hospede.getCreatedAt())
                .updatedAt(hospede.getUpdatedAt())
                .build();
    }
}

