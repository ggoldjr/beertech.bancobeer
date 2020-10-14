package com.br.beertech.messages;

public class TransferenciaMessage {

    private String hashContaOrigem;

    private String hashContaDestino;

    private Double valor;

    public String getHashContaOrigem() {
        return hashContaOrigem;
    }

    public void setHashContaOrigem(String hashContaOrigem) {
        this.hashContaOrigem = hashContaOrigem;
    }

    public String getHashContaDestino() {
        return hashContaDestino;
    }

    public void setHashContaDestino(String hashContaDestino) {
        this.hashContaDestino = hashContaDestino;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
