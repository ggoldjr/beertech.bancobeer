package br.com.api.exception;

import lombok.Getter;

public class ApplicationException extends RuntimeException {

    @Getter
    private Integer status;
    @Getter
    private String message;

    public ApplicationException(Integer status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
