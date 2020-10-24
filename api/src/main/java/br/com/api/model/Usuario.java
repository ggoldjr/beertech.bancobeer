package br.com.api.model;

import br.com.api.dto.UsuarioDto;
import br.com.api.spec.UsuarioSpec;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Usuario {

    public static Usuario criar(UsuarioSpec usuarioSpec){
        return Usuario.builder()
                .perfil(usuarioSpec.getPerfil())
                .cnpj(usuarioSpec.getCnpj())
                .email(usuarioSpec.getEmail())
                .nome(usuarioSpec.getNome())
                .senha(usuarioSpec.getSenha())
                .build();
    }

    public enum Perfil { USUARIO, ADMIN }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    String  nome;

    @NotBlank(message = "Senha não pode ser nula")
    String  senha;

    @NotBlank(message = "CNPJ não pode ser nulo")
    @Column(unique = true)
    String  cnpj;

    @NotBlank(message = "E-mail não pode ser nulo")
    @Column(unique = true)
    String  email;

    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    @OneToOne
    @JoinColumn(name = "conta_id")
    @JsonIgnore
    private Conta conta;

    private String contaHash;

    @CreatedDate
    private LocalDateTime criado_em;

    @LastModifiedDate
    private LocalDateTime atualizado_em;

    private boolean podeReceberDoacoes;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<Doacao> doacoes;

    public boolean podeDoar(Double saldo, Double valorParaDoar) {
        return saldo >= valorParaDoar && !podeReceberDoacoes;
    }


    public UsuarioDto toUsuarioDto() {
        return UsuarioDto.builder()
                .cnpj(this.cnpj)
                .email(this.email)
                .id(this.id)
                .nome(this.nome)
                .contaHash(this.contaHash)
                .perfil(this.perfil)
                .build();
    }
}
