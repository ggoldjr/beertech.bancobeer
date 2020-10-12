package br.com.api.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> notFoundException(NotFoundException notFoundException) {
        StandardError standardError = new StandardError(HttpStatus.NOT_FOUND.value(), notFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(standardError);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<StandardError> saldoInsuficiente(SaldoInsuficienteException saldoInsuficienteException) {
        StandardError standardError = new StandardError(HttpStatus.BAD_REQUEST.value(), saldoInsuficienteException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(standardError);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException nfe,
                                                               HttpHeaders headers,
                                                               HttpStatus status,
                                                               WebRequest request) {
        ErroValidacao erroValidacao = new ErroValidacao(HttpStatus.BAD_REQUEST.value(), "Erro de validação");
        nfe.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldErrorMessage(fieldError.getField(), fieldError.getDefaultMessage()))
                .forEach(erroValidacao::addError);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroValidacao);
    }
}