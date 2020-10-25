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
public class ListaContaPorIdTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Nested
    abstract class ListaContaPorIdSetup {
        ResponseEntity<String> responseEntity;
        ContaDto conta;
        ResponseError responseError;
        Long contaId;

        @BeforeEach
        void run() throws JsonProcessingException {
            usuarioSetup.setup();
            testUtil.login(port, getUsuarioLogado());
            contaId = getContaId();
            String url = String.format("http://localhost:%s/contas/id/%s", port, contaId);
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

        protected abstract Long getContaId();
    }


    @Nested
    class QuandoAdminListaConta extends ListaContaPorIdSetup {

        @Override
        protected Long getContaId() {
            return usuarioSetup.getUsuario1().getContaDto().getId();
        }

        @Test
        void deveRetornarContaComHashPesquisado() {
            assertThat(conta.getId()).isEqualTo(contaId);
        }
    }


    @Nested
    class QuandoUsuarioBuscaConta extends ListaContaPorIdSetup {

        @Override
        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Override
        protected Long getContaId() {
            return usuarioSetup.getUsuario1().getContaDto().getId();
        }

        @Test
        void deveRetornarContaComHashPesquisado() {
            assertThat(conta.getId()).isEqualTo(contaId);
        }
    }


    @Nested
    class QuandoUsuarioBuscaContaDeOutroUsuario extends ListaContaPorIdSetup {

        @Override
        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Override
        protected Long getContaId() {
            return usuarioSetup.getUsuario2().getContaDto().getId();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(401);
            assertThat(responseError.getMessage()).isEqualTo("Só pode buscar sua conta");
        }
    }
}
