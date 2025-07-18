package com.hotel.backend.exception;

/**
 * Exceção lançada quando um check-in não é encontrado.
 */
public class CheckinNaoEncontradoException extends RuntimeException {

    public CheckinNaoEncontradoException(String message) {
        super(message);
    }

    public CheckinNaoEncontradoException(Long id) {
        super("Check-in não encontrado com ID: " + id);
    }
}

