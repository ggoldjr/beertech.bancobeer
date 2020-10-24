package com.br.beertech.listeners;

import com.br.beertech.dto.TransacaoDto;
import com.br.beertech.messages.OperacaoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OperacaoListener {

  private final RestTemplate restTemplate;
  private static final Logger logger = LoggerFactory.getLogger(OperacaoListener.class);

  @Autowired
  public OperacaoListener(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @RabbitListener(queues = "operacao",containerFactory = "simpleContainerFactory")
  public void receive(@Payload OperacaoMessage operacaoMessage, @Header("Authorization") String auth){
    logger.info("enviando requisição para conta: {}", operacaoMessage.getContaHash());
    TransacaoDto transacaoDto = new TransacaoDto(operacaoMessage.getOperacao(),operacaoMessage.getValor());
    try{
      String url = String.format(System.getenv("API_URL") + "contas/%s/operacao", operacaoMessage.getContaHash());
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("Authorization", auth);
      httpHeaders.add("Content-Type", "application/json");
      restTemplate.postForObject(url, transacaoDto ,Void.class, httpHeaders);
    }catch (Exception e){
      logger.error("Error on try request", e);
    }
  }
}
