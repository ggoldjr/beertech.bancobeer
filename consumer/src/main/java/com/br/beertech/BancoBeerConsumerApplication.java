package com.br.beertech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class BancoBeerConsumerApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(BancoBeerConsumerApplication.class);
    springApplication.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
    springApplication.run(args);
  }

}