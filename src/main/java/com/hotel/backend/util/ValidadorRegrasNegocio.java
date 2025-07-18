package com.hotel.backend.util;

import com.hotel.backend.entity.Checkin;
import com.hotel.backend.entity.Hospede;
import com.hotel.backend.exception.HospedeJaNoHotelException;
import com.hotel.backend.repository.CheckinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Validador de regras de negócio.
 * Aplicando Validator Pattern e Single Responsibility Principle.
 * Centraliza validações complexas de negócio.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidadorRegrasNegocio {

    private final CheckinRepository checkinRepository;

    /**
     * Valida se um hóspede pode realizar check-in.
     * Verifica se não possui check-in ativo.
     */
    public void validarCheckinPermitido(Hospede hospede) {
        log.debug("Validando se hóspede {} pode realizar check-in", hospede.getNome());
        
        if (checkinRepository.hospedeTemCheckinAtivo(hospede)) {
            log.warn("Tentativa de check-in para hóspede {} que já está no hotel", hospede.getNome());
            throw new HospedeJaNoHotelException(hospede.getNome());
        }
    }

    /**
     * Valida se um check-in pode ser atualizado.
     * Verifica se ainda está ativo.
     */
    public void validarCheckinAtualizavel(Checkin checkin) {
        log.debug("Validando se check-in {} pode ser atualizado", checkin.getId());
        
        if (checkin.getDataSaida() != null) {
            log.warn("Tentativa de atualizar check-in {} já finalizado", checkin.getId());
            throw new IllegalStateException("Não é possível atualizar check-in já finalizado");
        }
    }

    /**
     * Valida se um checkout pode ser realizado.
     * Verifica se o check-in ainda está ativo.
     */
    public void validarCheckoutPermitido(Checkin checkin) {
        log.debug("Validando se checkout pode ser realizado para check-in {}", checkin.getId());
        
        if (checkin.getDataSaida() != null) {
            log.warn("Tentativa de checkout para check-in {} já finalizado", checkin.getId());
            throw new IllegalStateException("Check-in já foi finalizado");
        }
    }

    /**
     * Valida se as datas de entrada e saída são consistentes.
     */
    public void validarDatasConsistentes(LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        if (dataEntrada == null) {
            throw new IllegalArgumentException("Data de entrada é obrigatória");
        }

        if (dataSaida != null && dataSaida.isBefore(dataEntrada)) {
            log.warn("Data de saída {} anterior à data de entrada {}", dataSaida, dataEntrada);
            throw new IllegalArgumentException("Data de saída deve ser posterior à data de entrada");
        }

        if (dataEntrada.isAfter(LocalDateTime.now().plusDays(1))) {
            log.warn("Data de entrada {} muito no futuro", dataEntrada);
            throw new IllegalArgumentException("Data de entrada não pode ser muito no futuro");
        }
    }

    /**
     * Valida se um documento (CPF) tem formato válido.
     * Implementa validação básica de formato.
     */
    public void validarFormatoDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento é obrigatório");
        }

        // Remove caracteres não numéricos
        String documentoLimpo = documento.replaceAll("\\D", "");
        
        if (documentoLimpo.length() != 11) {
            log.warn("Documento {} com formato inválido", documento);
            throw new IllegalArgumentException("Documento deve conter exatamente 11 dígitos");
        }

        // Verifica se não são todos os dígitos iguais
        if (documentoLimpo.matches("(\\d)\\1{10}")) {
            log.warn("Documento {} com todos os dígitos iguais", documento);
            throw new IllegalArgumentException("Documento inválido");
        }
    }

    /**
     * Valida se um telefone tem formato válido.
     */
    public void validarFormatoTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone é obrigatório");
        }

        // Remove caracteres não numéricos
        String telefoneLimpo = telefone.replaceAll("\\D", "");
        
        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            log.warn("Telefone {} com formato inválido", telefone);
            throw new IllegalArgumentException("Telefone deve conter entre 10 e 11 dígitos");
        }
    }
}

