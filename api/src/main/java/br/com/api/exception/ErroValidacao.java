package br.com.api.exception;

import lombok.With;

import java.util.ArrayList;
import java.util.List;

@With
public class ErroValidacao extends StandardError {

    public final List<FieldErrorMessage> errors = new ArrayList<>();

    public ErroValidacao(Integer status, String message) {
        super(status, message);
    }

    public void addError(FieldErrorMessage fieldErrorMessage) {
        errors.add(fieldErrorMessage);
    }
}