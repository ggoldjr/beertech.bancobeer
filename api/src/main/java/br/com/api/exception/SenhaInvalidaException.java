package br.com.api.exception;

public class SenhaInvalidaException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -8423178730199769340L;

    public SenhaInvalidaException( ) {
        super("Senha inválida");
    }
}
