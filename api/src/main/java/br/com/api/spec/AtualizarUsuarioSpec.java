package br.com.api.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class AtualizarUsuarioSpec {
    @NotNull(message = "não pode ser nulo")
    private String  nome;
    @NotNull(message = "não pode ser nulo")
    @CNPJ(message = "cnpj inválido")
    private String  cnpj;
    @NotNull(message = "não pode ser nulo")
    private String  email;
}
