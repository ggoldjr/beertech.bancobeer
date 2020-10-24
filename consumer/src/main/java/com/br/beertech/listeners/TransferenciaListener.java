package com.br.beertech.listeners;

import com.br.beertech.dto.TransferenciaDto;
import com.br.beertech.messages.TransferenciaMessage;
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
public class TransferenciaListener {

    private static final Logger logger = LoggerFactory.getLogger(TransferenciaListener.class);
    private static final String URL = String.format(Optional.ofNullable(System.getenv("API_URL")).orElse("http://localhost:8080/")
                                            + "contas/operacoes/tranferencias");

    private final RestTemplate restTemplate;
    public ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    public TransferenciaListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RabbitListener(queues = "transferencia",containerFactory = "simpleContainerFactory")
    public void receive(@Payload TransferenciaMessage transferenciaMessage, @Header("Authorization") String auth){
        logger.info("enviando trasferencia de {} para {}", transferenciaMessage.getHashContaOrigem(), transferenciaMessage.getHashContaDestino());
        try{
            TransferenciaDto transferenciaDto = TransferenciaDto.criar(transferenciaMessage);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", auth);
            httpHeaders.add("Content-Type", "application/json");
            HttpEntity<String> stringHttpEntity = new HttpEntity<>(mapper.writeValueAsString(transferenciaDto), httpHeaders);
            restTemplate.exchange(URL, HttpMethod.POST, stringHttpEntity ,Void.class);
        }catch (Exception e){
            logger.error("Error on try request:", e);
        }
    }
}
