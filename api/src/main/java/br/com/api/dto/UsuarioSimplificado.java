package br.com.api.dto;

import br.com.api.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class UsuarioSimplificado {
    private Long id;
    @NotBlank(message = "Nome do usuário não pode ser nulo.")
    private String  nome;
    @NotBlank(message = "Cnpj do usuário não pode ser nulo.")
    private String  cnpj;
    @NotBlank(message = "E-mail do usuário não pode ser nulo.")
    private String  email;
    private Usuario.Perfil perfil;
}
