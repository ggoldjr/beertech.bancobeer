package br.com.api.dto;

import br.com.api.model.Usuario;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UsuarioDto {

    private String id;

    private String  nome;

    private String  cnpj;

    private String  email;

    private Usuario.Perfil perfil;

}
