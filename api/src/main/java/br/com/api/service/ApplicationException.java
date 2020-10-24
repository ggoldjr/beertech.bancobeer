package br.com.api.service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApplicationException extends RuntimeException {

    private Integer code;
    private String message;
}
