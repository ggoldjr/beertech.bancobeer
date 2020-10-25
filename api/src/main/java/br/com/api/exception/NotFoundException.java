package br.com.api.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String nomeModelo) {
        super(String.format("%s não encontrado", nomeModelo));
    }
}