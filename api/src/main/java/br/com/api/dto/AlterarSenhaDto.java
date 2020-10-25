package br.com.api.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class AlterarSenhaDto {

    @NotNull(message = "ID não pode ser nulo.")
    @NotEmpty(message = "SID não pode ser vazio.")
    private Long idUsuario;
    @NotNull(message = "Senha antiga não pode ser nula.")
    @NotEmpty(message = "Senha antiga não pode ser vazia.")
    private String senhaAntiga;
    @NotNull(message = "Senha nova não pode ser  nula.")
    @NotEmpty(message = "Senha nova não pode ser vazia.")
    private String senhaNova;
}
