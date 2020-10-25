package br.com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterarSenhaDto {

    @NotNull(message = "{usuario.id.notnull}")
    private Long idUsuario;
    @NotBlank(message ="{senha.antiga.notblank}" )
    private String senhaAntiga;
    @NotBlank(message ="{senha.nova.notblank}" )
    private String senhaNova;
}
