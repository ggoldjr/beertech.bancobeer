package br.com.api.model;


import br.com.api.dto.OperacaoDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Operacao implements Serializable {

    public static Operacao criar(OperacaoDto operacaoDto, Conta conta) {
        return Operacao.builder()
                .tipo(Tipo.valueOf(operacaoDto.getTipo().toUpperCase()))
                .valor(operacaoDto.getValor())
                .dataOperacao(LocalDate.now())
                .conta(conta)
                .build();
    }

    public enum Tipo { DEPOSITO, TRANSFERENCIA, DOACAO, TRANSFERENCIA_RECEBIDA, DOACAO_RECEBIDA, BONUS }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private LocalDate dataOperacao;

    @JsonIgnore
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

    public boolean tipoEtransferenciaOUdoacao() {
        return tipo == Tipo.DOACAO || tipo == Tipo.TRANSFERENCIA;
    }
}
