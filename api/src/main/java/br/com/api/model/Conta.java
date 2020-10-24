package br.com.api.model;

import br.com.api.dto.ContaDto;
import br.com.api.dto.UsuarioSimplificado;
import br.com.api.exception.SaldoInsuficienteException;
import br.com.api.exception.ApplicationException;
import br.com.api.spec.ContaSpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Conta implements Serializable {

    public static Conta criar(ContaSpec contaSpec, Usuario usuario) {
        return Conta.builder()
                .hash(UUID.randomUUID().toString())
                .saldo(0d)
                .usuario(usuario)
                .build();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Min(value = 0)
    private Double saldo;

    @NotBlank
    @Column(unique = true)
    private String hash;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Operacao> operacoes;

    @CreatedDate
    private LocalDateTime criado_em;

    @LastModifiedDate
    private LocalDateTime atualizado_em;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Conta() {}

    public Double getSaldo() {
        return Optional.ofNullable(saldo).orElse(0d);
    }

    public Double saque(Double valor, Usuario usuario) {
        if (!usuario.getContaHash().equals(this.hash)) throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Não pode sacar de outras contas");
        if (!saldoEmaiorOrIgualA(valor)) throw new SaldoInsuficienteException();
        this.setSaldo(this.saldo + valor *-1);
        return valor;
    }

    public void deposito(Double valor) {
        this.setSaldo(this.saldo + valor);
    }

    public boolean saldoEmaiorOrIgualA(Double valor) {
        return this.saldo >= valor;
    }

    public ContaDto toContaDto() {
        UsuarioSimplificado usuarioSimplificado = UsuarioSimplificado.builder()
                .nome(this.usuario.getNome())
                .email(this.usuario.getEmail())
                .cnpj(this.usuario.getCnpj())
                .id(this.usuario.getId())
                .perfil(this.usuario.getPerfil())
                .build();
        return ContaDto.builder()
                .hash(this.hash)
                .id(this.id)
                .saldo(this.saldo)
                .usuario(usuarioSimplificado)
                .build();
    }
}
