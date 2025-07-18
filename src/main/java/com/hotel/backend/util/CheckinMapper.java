package com.hotel.backend.util;

import com.hotel.backend.dto.CheckinRequestDTO;
import com.hotel.backend.dto.CheckinResponseDTO;
import com.hotel.backend.dto.HospedeResponseDTO;
import com.hotel.backend.entity.Checkin;
import com.hotel.backend.entity.Hospede;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre entidades Checkin e DTOs.
 * Aplicando Mapper Pattern e delegando conversões específicas.
 */
@Component
@RequiredArgsConstructor
public class CheckinMapper {

    private final HospedeMapper hospedeMapper;

    /**
     * Converte CheckinRequestDTO para entidade Checkin.
     * Usado na criação de novos check-ins.
     */
    public Checkin toEntity(CheckinRequestDTO dto, Hospede hospede) {
        if (dto == null) {
            return null;
        }

        return Checkin.builder()
                .hospede(hospede)
                .dataEntrada(dto.getDataEntrada())
                .dataSaida(dto.getDataSaida())
                .adicionalVeiculo(dto.getAdicionalVeiculo() != null ? dto.getAdicionalVeiculo() : false)
                .build();
    }

    /**
     * Converte entidade Checkin para CheckinResponseDTO.
     * Inclui informações do hóspede e status calculados.
     */
    public CheckinResponseDTO toResponseDTO(Checkin entity) {
        if (entity == null) {
            return null;
        }

        HospedeResponseDTO hospedeDTO = hospedeMapper.toSimpleResponseDTO(entity.getHospede());

        return CheckinResponseDTO.builder()
                .id(entity.getId())
                .hospede(hospedeDTO)
                .dataEntrada(entity.getDataEntrada())
                .dataSaida(entity.getDataSaida())
                .adicionalVeiculo(entity.getAdicionalVeiculo())
                .valorTotal(entity.getValorTotal())
                .ativo(entity.isAtivo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Atualiza entidade Checkin com dados do DTO.
     * Usado na atualização de check-ins existentes.
     */
    public void updateEntityFromDTO(Checkin entity, CheckinRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setDataEntrada(dto.getDataEntrada());
        entity.setAdicionalVeiculo(dto.getAdicionalVeiculo() != null ? dto.getAdicionalVeiculo() : false);
        
        // Se data de saída foi informada, realiza checkout
        if (dto.getDataSaida() != null) {
            entity.realizarCheckout(dto.getDataSaida());
        }
    }
}

