package com.hotel.backend.exception;

/**
 * Exceção lançada quando se tenta cadastrar um hóspede com documento já existente.
 */
public class DocumentoJaCadastradoException extends RuntimeException {

    public DocumentoJaCadastradoException(String documento) {
        super("Já existe um hóspede cadastrado com o documento: " + documento);
    }
}

