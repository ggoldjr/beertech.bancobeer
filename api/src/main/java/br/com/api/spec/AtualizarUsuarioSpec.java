package br.com.api.spec;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class AtualizarUsuarioSpec {
    @NotNull(message = "não pode ser nulo")
    private String  nome;
    @NotNull(message = "não pode ser nulo")
    @CNPJ(message = "cnpj inválido")
    private String  cnpj;
    @NotNull(message = "não pode ser nulo")
    private String  email;
}
