package br.com.api.model;


import br.com.api.dto.OperacaoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@Builder
public class Operacao implements Serializable {

    public static Operacao criar(OperacaoDto operacaoDto, Conta conta) {
        return Operacao.builder()
                .tipo(Tipo.valueOf(operacaoDto.getTipo().toUpperCase()))
                .valor(operacaoDto.getValor())
                .dataOperacao(LocalDateTime.now())
                .conta(conta)
                .build();
    }

    public enum Tipo { DEPOSITO, SAQUE }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double valor;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private LocalDateTime dataOperacao;

    @ManyToOne
    @JoinColumn(name = "conta_hash" )
    private Conta conta;

    public Operacao() {
    }
}
