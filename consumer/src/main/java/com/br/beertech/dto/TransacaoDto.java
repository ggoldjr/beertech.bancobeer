package com.br.beertech.dto;

public class TransacaoDto {
    private String tipo;
    private Double valor;

    public TransacaoDto(String tipo, Double valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
