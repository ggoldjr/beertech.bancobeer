package br.com.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExtratoDto {

    @Positive(message = "Valor deve ser maior que zero")
    private Double valor;
    private String tipo;
    private LocalDateTime data;
    private String hashContaOrigem;
    private String hashContaDestino;
}
