package com.br.beertech.listeners;

import com.br.beertech.dto.TransacaoDto;
import com.br.beertech.messages.OperacaoMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class OperacaoListener {

  private final RestTemplate restTemplate;
  private static final Logger logger = LoggerFactory.getLogger(OperacaoListener.class);
  public ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Autowired
  public OperacaoListener(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @RabbitListener(queues = "operacao",containerFactory = "simpleContainerFactory")
  public void receive(@Payload OperacaoMessage operacaoMessage, @Header("Authorization") String auth){
    logger.info("enviando requisição para conta: {}", operacaoMessage.getContaHash());
    TransacaoDto transacaoDto = new TransacaoDto(operacaoMessage.getOperacao(),operacaoMessage.getValor());
    try{
      String url = String.format(Optional.ofNullable(System.getenv("API_URL")).orElse("http://localhost:8080/") + "contas/%s/operacoes/depositos", operacaoMessage.getContaHash());
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("Authorization", auth);
      httpHeaders.add("Content-Type", "application/json");
      HttpEntity<String> stringHttpEntity = new HttpEntity<>(mapper.writeValueAsString(transacaoDto), httpHeaders);
      restTemplate.exchange(url, HttpMethod.POST, stringHttpEntity ,Void.class);
    }catch (Exception e){
      logger.error("Error on try request", e);
    }
  }
}
