package com.hotel.backend.util;

import com.hotel.backend.dto.HospedeRequestDTO;
import com.hotel.backend.dto.HospedeResponseDTO;
import com.hotel.backend.entity.Hospede;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mapper para conversão entre entidades Hospede e DTOs.
 * Aplicando Mapper Pattern e Single Responsibility Principle.
 * Centraliza a lógica de conversão e facilita manutenção.
 */
@Component
public class HospedeMapper {

    /**
     * Converte HospedeRequestDTO para entidade Hospede.
     * Usado na criação de novos hóspedes.
     */
    public Hospede toEntity(HospedeRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Hospede.builder()
                .nome(dto.getNome())
                .documento(dto.getDocumento())
                .telefone(dto.getTelefone())
                .build();
    }

    /**
     * Converte entidade Hospede para HospedeResponseDTO.
     * Inclui informações calculadas como valores gastos.
     */
    public HospedeResponseDTO toResponseDTO(Hospede entity, 
                                          BigDecimal valorTotalGasto, 
                                          BigDecimal valorUltimaHospedagem, 
                                          Boolean estaNoHotel) {
        if (entity == null) {
            return null;
        }

        return HospedeResponseDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .documento(entity.getDocumento())
                .telefone(entity.getTelefone())
                .valorTotalGasto(valorTotalGasto != null ? valorTotalGasto : BigDecimal.ZERO)
                .valorUltimaHospedagem(valorUltimaHospedagem != null ? valorUltimaHospedagem : BigDecimal.ZERO)
                .estaNoHotel(estaNoHotel != null ? estaNoHotel : false)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Converte entidade Hospede para HospedeResponseDTO simples.
     * Usado quando não há necessidade de informações calculadas.
     */
    public HospedeResponseDTO toSimpleResponseDTO(Hospede entity) {
        return toResponseDTO(entity, BigDecimal.ZERO, BigDecimal.ZERO, false);
    }

    /**
     * Atualiza entidade Hospede com dados do DTO.
     * Usado na atualização de hóspedes existentes.
     */
    public void updateEntityFromDTO(Hospede entity, HospedeRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setNome(dto.getNome());
        entity.setDocumento(dto.getDocumento());
        entity.setTelefone(dto.getTelefone());
    }
}

