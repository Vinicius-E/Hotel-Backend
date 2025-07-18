package com.hotel.backend.exception;

/**
 * Exceção lançada quando um hóspede não é encontrado.
 * Aplicando princípios de Clean Code: nome autoexplicativo e responsabilidade específica.
 */
public class HospedeNaoEncontradoException extends RuntimeException {

    public HospedeNaoEncontradoException(String message) {
        super(message);
    }

    public HospedeNaoEncontradoException(Long id) {
        super("Hóspede não encontrado com ID: " + id);
    }

    public HospedeNaoEncontradoException(String campo, String valor) {
        super("Hóspede não encontrado com " + campo + ": " + valor);
    }
}

