package br.com.api.dto;

import br.com.api.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {

    private Long id;
    @NotBlank(message = "{usuario.nome.notblank}")
    private String  nome;
    @NotBlank(message = "usurio.cnpj.notblank")
    private String  cnpj;
    @NotBlank(message = "{usuario.email.notblank}")
    private String  email;
    private ContaDto contaDto;
    private Boolean podeReceberDoacoes;
    private Usuario.Perfil perfil;
}
