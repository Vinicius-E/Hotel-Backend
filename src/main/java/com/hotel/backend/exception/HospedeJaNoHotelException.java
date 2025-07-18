package com.hotel.backend.exception;

/**
 * Exceção lançada quando se tenta fazer check-in de um hóspede que já está no hotel.
 */
public class HospedeJaNoHotelException extends RuntimeException {

    public HospedeJaNoHotelException(String nomeHospede) {
        super("O hóspede " + nomeHospede + " já possui um check-in ativo no hotel");
    }

    public HospedeJaNoHotelException(Long hospedeId) {
        super("O hóspede com ID " + hospedeId + " já possui um check-in ativo no hotel");
    }
}

