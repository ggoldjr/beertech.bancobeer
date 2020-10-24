package br.com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Builder
@Data
public class HabilitarOrDesabilitarDoacaoDto {

    @NotNull(message = "ID não pode ser nulo.")
    private Long idUsuario;
    @NotNull(message = "Poder receber doação não pode ser nulo.")
    private Boolean podeReceberDoacao;
}
