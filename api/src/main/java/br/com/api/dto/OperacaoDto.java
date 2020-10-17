package br.com.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperacaoDto {

    private String tipo;

    @Positive(message = "Valor deve ser maior que zero")
    private Double valor;

}
