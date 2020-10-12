package com.br.beertech.listeners;

import com.br.beertech.dto.TransacaoDto;
import com.br.beertech.dto.TransferenciaDto;
import com.br.beertech.messages.OperacaoMessage;
import com.br.beertech.messages.TransferenciaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransferenciaListener {

    private static final Logger logger = LoggerFactory.getLogger(TransferenciaListener.class);
    private final RestTemplate restTemplate;

    @Autowired
    public TransferenciaListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RabbitListener(queues = "transferencia",containerFactory = "simpleContainerFactory")
    public void receive(@Payload TransferenciaMessage transferenciaMessage){
        logger.info("enviando trasferencia de {} para {}", transferenciaMessage.getHashContaOrigem(), transferenciaMessage.getHashContaDestino());
        String url = String.format("http://localhost:8080/transferencias");
        try{
            TransferenciaDto transferenciaDto = new TransferenciaDto();
            transferenciaDto.setHashContaOrigem(transferenciaMessage.getHashContaOrigem());
            transferenciaDto.setHashContaDestino(transferenciaMessage.getHashContaDestino());
            transferenciaDto.setValor(transferenciaMessage.getValor());
            restTemplate.postForObject(url, transferenciaDto, Void.class);
        }catch (Exception e){
            logger.error("Error on try request:", e);
        }
    }
}
