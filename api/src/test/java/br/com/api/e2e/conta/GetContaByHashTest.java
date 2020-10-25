package br.com.api.e2e.conta;

import br.com.api.dto.ContaDto;
import br.com.api.seed.UsuarioSetup;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetContaByHashTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Nested
    abstract class GetContaByHashSetup {
        ResponseEntity<String> responseEntity;
        ContaDto conta;
        ResponseError responseError;
        String hash;

        @BeforeEach
        void run() throws JsonProcessingException {
            usuarioSetup.setup();
            testUtil.login(port, getUsuarioLogado());
            hash = getHash();
            String url = String.format("http://localhost:%s/contas/%s", port, hash);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity();
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                conta = testUtil.mapper.readValue(responseEntity.getBody(), ContaDto.class);
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUsuarioLogado() {
            return usuarioSetup.getAdmin().getEmail();
        }

        protected abstract String getHash();
    }


    @Nested
    class QuandoAdminListaConta extends GetContaByHashSetup {

        @Override
        protected String getHash() {
            return usuarioSetup.getUsuario1().getContaHash();
        }

        @Test
        void deveRetornarContaComHashPesquisado() {
            assertThat(conta.getHash()).isEqualTo(hash);
        }
    }


    @Nested
    class QuandoUsuarioBuscaConta extends GetContaByHashSetup {

        @Override
        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Override
        protected String getHash() {
            return usuarioSetup.getUsuario1().getContaHash();
        }

        @Test
        void deveRetornarContaComHashPesquisado() {
            assertThat(conta.getHash()).isEqualTo(hash);
        }
    }


    @Nested
    class QuandoUsuarioBuscaContaDeOutroUsuario extends GetContaByHashSetup {

        @Override
        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Override
        protected String getHash() {
            return usuarioSetup.getUsuario2().getContaHash();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(401);
            assertThat(responseError.getMessage()).isEqualTo("Só pode buscar sua conta");
        }
    }
}