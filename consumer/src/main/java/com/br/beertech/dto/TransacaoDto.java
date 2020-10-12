package com.br.beertech.dto;

public class TransacaoDto {
    private String operacao;
    private Double valor;

    public TransacaoDto(String operacao, Double valor) {
        this.operacao = operacao;
        this.valor = valor;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        operacao = operacao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
