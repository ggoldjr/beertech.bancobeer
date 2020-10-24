package br.com.api.e2e.conta;

import br.com.api.dto.ContaDto;
import br.com.api.seed.UsuarioSetup;
import br.com.api.spec.ContaSpec;
import br.com.api.util.ResponseError;
import br.com.api.util.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CriarContaTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Nested
    abstract class CriarContaSetup {
        ResponseEntity<String> responseEntity;
        ContaDto contaCriada;
        ResponseError responseError;

        @BeforeEach
        void setup() throws JsonProcessingException {
            usuarioSetup.setup();
            testUtil.login(port, usuarioSetup.getUsuario1().getEmail());
            String url = String.format("http://localhost:%s/contas", port);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(ContaSpec.builder().idUsuario(1l).build());
            responseEntity = testUtil.restTemplate.postForEntity(url, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 201) {
                contaCriada = testUtil.parseSuccessfulResponse(responseEntity, ContaDto.class);
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
