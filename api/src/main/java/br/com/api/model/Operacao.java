package br.com.api.model;


import br.com.api.dto.OperacaoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document("operacoes")
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

    public enum Tipo { DEPOSITO, SAQUE, TRANSFERENCIA }
    @Id
    private String _id;
    private double valor;
    private Tipo tipo;
    private LocalDateTime dataOperacao;
    private Conta conta;
    private String hashContaDestino;
    private LocalDateTime criado_em;
    private LocalDateTime atualizado_em;
}
