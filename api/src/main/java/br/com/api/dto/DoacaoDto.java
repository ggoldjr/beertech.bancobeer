package br.com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Data
public class DoacaoDto {

    private Long idUsuarioBeneficiario;
    private BigDecimal valorDoado;
}