package br.com.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaldoDto {

    private String hash;
    private Double saldo;

}
