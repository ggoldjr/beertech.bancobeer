package br.com.api.e2e.conta;

import br.com.api.dto.ContaDto;
import br.com.api.dto.SaldoDto;
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
public class GetSaldoTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Nested
    abstract class GetSaldoSetup {
        ResponseEntity<String> responseEntity;
        Double saldo;
        ResponseError responseError;
        String hash;

        @BeforeEach
        void run() throws JsonProcessingException {
            usuarioSetup.setup();
            testUtil.login(port, getUsuarioLogado());
            hash = getHash();
            String url = String.format("http://localhost:%s/contas/%s/saldos", port, hash);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity();
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                saldo = testUtil.mapper.readValue(responseEntity.getBody(), Double.class);
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
    class QuandoAdminBuscaSaldoDaConta extends GetSaldoSetup {

        @Override
        protected String getHash() {
            return usuarioSetup.getUsuario1().getContaHash();
        }

        @Test
        void deveRetornarSaldoIgualA2000() {
            assertThat(saldo).isEqualTo(2000);
        }
    }


    @Nested
    class QuandoUsuarioBuscaSaldoDaSuaConta extends GetSaldoSetup {

        @Override
        protected String getHash() {
            return usuarioSetup.getUsuario1().getContaHash();
        }

        @Override
        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Test
        void deveRetornarSaldoIgualA2000() {
            assertThat(saldo).isEqualTo(2000);
        }
    }

    @Nested
    class QuandoUsuarioBuscaSaldoDeOutraConta extends GetSaldoSetup {

        @Override
        protected String getHash() {
            return usuarioSetup.getUsuario2().getContaHash();
        }

        @Override
        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(401);
            assertThat(responseError.getMessage()).isEqualTo("Não é permitido ver saldo de outra conta");
        }
    }
}