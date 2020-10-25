package br.com.api.dto;

import br.com.api.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    private Long id;
    private String  nome;
    private String  cnpj;
    private String  email;
    private ContaDto contaDto;
    private Boolean podeReceberDoacoes;
    private Usuario.Perfil perfil;
}
