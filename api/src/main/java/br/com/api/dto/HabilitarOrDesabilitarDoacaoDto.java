package br.com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class HabilitarOrDesabilitarDoacaoDto {

    @NotNull(message = "{usuario.id.notnull}")
    private Long idUsuario;
    @NotNull(message = "{usuario.podereceberdoacao.notnull}")
    private Boolean podeReceberDoacao;
}