package br.com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterarSenhaDto {

    @NotNull(message = "ID não pode ser nulo.")
    private Long idUsuario;
    @NotNull(message = "Senha antiga não pode ser nulo.")
    private String senhaAntiga;
    @NotNull(message = "Senha nova não pode ser nulo.")
    private String senhaNova;
}
