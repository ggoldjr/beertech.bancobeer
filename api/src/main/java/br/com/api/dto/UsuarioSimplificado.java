package br.com.api.dto;

import br.com.api.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class UsuarioSimplificado {
    private Long id;
    private String  nome;
    private String  cnpj;
    private String  email;
    private Usuario.Perfil perfil;
}
