package com.br.beertech.dto;

import com.br.beertech.messages.TransferenciaMessage;

public class TransferenciaDto {

    public static TransferenciaDto criar(TransferenciaMessage transferenciaMessage) {
        TransferenciaDto transferenciaDto = new TransferenciaDto();
        transferenciaDto.setHashContaOrigem(transferenciaMessage.getHashContaOrigem());
        transferenciaDto.setHashContaDestino(transferenciaMessage.getHashContaDestino());
        transferenciaDto.setValor(transferenciaMessage.getValor());
        return transferenciaDto;
    }

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
