package br.com.api.model;

import br.com.api.exception.SaldoInsuficienteException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Document(collection = "contas")
@Data
@Builder
@AllArgsConstructor
public class Conta implements Serializable {

    @Id
    private String _id;

    @NotNull
    @Min(value = 0)
    private Double saldo;

    @NotBlank
    private String hash;
    private LocalDateTime criado_em;
    private LocalDateTime atualizado_em;
    private Usuario usuario;

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
