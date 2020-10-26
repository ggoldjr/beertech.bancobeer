package br.com.api.e2e.conta;

import br.com.api.dto.ExtratoDto;
import br.com.api.seed.UsuarioSetup;
import br.com.api.util.ResponseError;
import br.com.api.util.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetExtratoTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Nested
    abstract class GetExtratoSetup {
        ResponseEntity<String> responseEntity;
        List<ExtratoDto> extrato;
        ResponseError responseError;
        String hash;

        @BeforeEach
        void run() throws JsonProcessingException {
            usuarioSetup.setup();
            testUtil.login(port, getUsuarioLogado());
            hash = getHash();
            String url = String.format("http://localhost:%s/contas/%s/extratos", port, hash);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity();
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                extrato = testUtil.mapper.readValue(responseEntity.getBody(), new TypeReference<>() {
                });
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        protected abstract String getHash();
    }


    @Nested
    class QuandoUsuarioListaSeuExtrato extends GetExtratoSetup {

        @Override
        protected String getHash() {
            return usuarioSetup.getUsuario1().getContaHash();
        }

        @Test
        void deveRetornarExtrato() {
            assertThat(extrato.size()).isEqualTo(1);
        }
    }


    @Nested
    class QuandoUsuarioListaExtratoDeOutroUsuario extends GetExtratoSetup {

        @Override
        protected String getHash() {
            return usuarioSetup.getUsuario2().getContaHash();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(401);
            assertThat(responseError.getMessage()).isEqualTo("Não pode ver extrato de outras contas");
        }
    }
}
