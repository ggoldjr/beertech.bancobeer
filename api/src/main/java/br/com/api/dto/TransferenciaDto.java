package br.com.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
public class TransferenciaDto {

    @NotNull(message = "Deve ser diferente de nulo")
    private String hashContaOrigem;

    @NotNull(message = "Deve ser diferente de nulo")
    private String hashContaDestino;

    @NotNull(message = "Deve ser diferente de nulo")
    @Positive(message = "Valor deve ser maior que zero")
    private BigDecimal valor;
}
