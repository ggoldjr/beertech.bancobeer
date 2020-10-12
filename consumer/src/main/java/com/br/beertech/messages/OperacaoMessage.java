package com.br.beertech.messages;

public class OperacaoMessage {

  private String contaHash;
  private String operacao;
  private Double valor;

  public String getContaHash() {
    return contaHash;
  }

  public void setContaHash(String contaHash) {
    this.contaHash = contaHash;
  }

  public String getOperacao() {
    return operacao;
  }

  public void setOperacao(String operacao) {
    this.operacao = operacao;
  }

  public Double getValor() {
    return valor;
  }

  public void setValor(Double valor) {
    this.valor = valor;
  }

  @Override
  public String toString() {
    return "OperacaoMessage{" +
        "contaHash='" + contaHash + '\'' +
        ", operacao='" + operacao + '\'' +
        ", valor=" + valor +
        '}';
  }
}
