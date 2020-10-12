package br.com.api.conta;

import br.com.api.model.Conta;
import br.com.api.util.ResponseError;
import br.com.api.util.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CriarContaTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Nested
    abstract class CriarContaSetup {
        ResponseEntity<String> responseEntity;
        Conta contaCriada;
        ResponseError responseError;

        @BeforeEach
        void setup() throws JsonProcessingException {
            String url = String.format("http://localhost:%s/contas", port);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity();
            responseEntity = testUtil.restTemplate.postForEntity(url, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                contaCriada = testUtil.parseSuccessfulResponse(responseEntity, Conta.class);
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }
    }

    @Nested
    class CriarConta extends CriarContaSetup {

        @Test
        void deveRetornarContaComId() {
            assertThat(contaCriada.getId()).isNotNull();
        }

        @Test
        void deveRetornaContaComHash() {
            assertThat(contaCriada.getHash()).isNotBlank();
        }

        @Test
        void deveRetornaContaComSaldoZero() {
            assertThat(contaCriada.getSaldo()).isZero();
        }
    }
}
