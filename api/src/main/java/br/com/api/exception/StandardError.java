package br.com.api.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StandardError {

    public final Integer status;
    public final String message;

}
