package br.com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Data
public class DoacaoDto {

    @NotNull(message = "{doacao.beneficiario.id.notnull}")
    private Long idUsuarioBeneficiario;
    @NotNull(message = "{doacao.valor.notnull}")
    private BigDecimal valorDoado;
}