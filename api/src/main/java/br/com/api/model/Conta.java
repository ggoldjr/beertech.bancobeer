package br.com.api.model;

import br.com.api.exception.SaldoInsuficienteException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Conta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Min(value = 0)
    private Double saldo;

    @NotBlank
    private String hash;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Operacao> operacoes;

    @OneToMany(mappedBy = "contaOrigem", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Transferencia> transferencias;

    public Conta() {}

    public Double getSaldo() {
        return Optional.ofNullable(saldo).orElse(0d);
    }

    public Double saque(Double valor) {
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
}
