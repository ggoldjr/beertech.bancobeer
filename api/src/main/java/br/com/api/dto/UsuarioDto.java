package br.com.api.dto;

import br.com.api.model.Usuario;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class UsuarioDto {
    private Long id;
    private String  nome;
    private String  cnpj;
    private String  email;
    private String  contaHash;
    private Usuario.Perfil perfil;
}
