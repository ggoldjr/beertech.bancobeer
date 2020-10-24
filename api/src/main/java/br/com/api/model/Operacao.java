package br.com.api.model;


import br.com.api.dto.OperacaoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Operacao implements Serializable {

    public static Operacao criar(OperacaoDto operacaoDto, Conta conta) {
        return Operacao.builder()
                .tipo(Tipo.valueOf(operacaoDto.getTipo().toUpperCase()))
                .valor(operacaoDto.getValor())
                .dataOperacao(LocalDateTime.now())
                .conta(conta)
                .build();
    }

    public enum Tipo { DEPOSITO, SAQUE, TRANSFERENCIA, DOACAO, TRANSFERENCIA_RECEBIDA, DOACAO_RECEBIDA, BONUS }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double valor;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private LocalDateTime dataOperacao;

    @ManyToOne
    @JoinColumn(name = "conta_id" )
    private Conta conta;

    private String hashContaDestino;

    @CreatedDate
    private LocalDateTime criado_em;

    @LastModifiedDate
    private LocalDateTime atualizado_em;

    public Operacao() {
    }
}
