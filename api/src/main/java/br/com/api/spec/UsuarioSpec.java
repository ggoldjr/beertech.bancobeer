package br.com.api.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioSpec {

    @NotNull(message = "Nome não pode ser nulo.")
    private String  nome;
    @NotNull(message = "CNPJ não pode ser nulo.")
    @CNPJ(message = "CNPJ inválido.")
    private String  cnpj;
    @NotNull(message = "E-mail não pode ser nulo;")
    private String  email;
    @NotNull(message = "Senha não pode ser nulo.")
    private String senha;
}
