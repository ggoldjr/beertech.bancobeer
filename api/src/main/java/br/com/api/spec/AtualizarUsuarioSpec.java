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
    private String  nome;
    private String  cnpj;
    private String  email;
}
