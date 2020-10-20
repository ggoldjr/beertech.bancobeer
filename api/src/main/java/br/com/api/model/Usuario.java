package br.com.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document(collation = "usuarios")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    public enum Perfil { USUARIO, ADMIN }

    @Id
    private String _id;

    @NotBlank
    String  nome;

    @NotBlank(message = "Senha não pode ser nula")
    String  senha;

    @NotBlank(message = "CNPJ não pode ser nulo")
    String  cnpj;

    @NotBlank(message = "E-mail não pode ser nulo")
    String  email;
    private Perfil perfil;
    private Conta conta;
    private LocalDateTime criado_em;
    private LocalDateTime atualizado_em;
}