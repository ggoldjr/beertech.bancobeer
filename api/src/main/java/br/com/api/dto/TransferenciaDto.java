package br.com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferenciaDto {

    @NotBlank(message = "{trasnferencia.hashorigem.notblank}")
    private String hashContaOrigem;

    @NotBlank(message = "trasnferencia.hahsdestino.notblank")
    private String hashContaDestino;

    @NotNull(message = "{tranferencia.valor.notnull}")
    @Positive(message = "{tranferencia.valor.notnull}")
    private BigDecimal valor;
}
