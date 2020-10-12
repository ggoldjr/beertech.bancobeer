package br.com.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class OperacaoDto {

    @NotBlank(message = "Operação inválida")
    private String tipo;

    @Positive(message = "Valor deve ser maior que zero")
    private Double valor;
}
