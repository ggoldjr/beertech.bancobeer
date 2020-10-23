package br.com.api.dto;

import br.com.api.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDtoIn {

    private String  nome;

    private String  cnpj;

    private String  email;

    private Usuario.Perfil perfil;

    private String senha;

}
