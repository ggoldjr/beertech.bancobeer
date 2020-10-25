package br.com.api.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@Builder
public class AtualizarUsuarioSpec {
    @NotNull(message = "Nome não pode ser nulo.")
    private String  nome;
    @NotNull(message = "CNPJ não pode ser nulo.")
    @CNPJ(message = "CNPJ inválido.")
    private String  cnpj;
    @NotNull(message = "E-mail não pode ser nulo.")
    private String  email;
}
