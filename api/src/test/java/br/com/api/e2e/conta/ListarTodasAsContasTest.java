package br.com.api.e2e.conta;

import br.com.api.dto.ContaDto;
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
public class ListarTodasAsContasTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;


    @Nested
    abstract class ListarTodasAsContasSetup {
        ResponseEntity<String> responseEntity;
        List<ContaDto> contas;
        ResponseError responseError;

        @BeforeEach
        void run() throws JsonProcessingException {
            usuarioSetup.setup();
            testUtil.login(port, getUsuarioLogado());
            String url = String.format("http://localhost:%s/contas", port);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity();
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                contas = testUtil.mapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUsuarioLogado() {
            return usuarioSetup.getAdmin().getEmail();
        }
    }


    @Nested
    class QuandoAdminListaTodasAsContas extends ListarTodasAsContasSetup {

        @Test
        void deveRetornar2contas() {
            assertThat(contas.size()).isEqualTo(2);
        }
    }

    @Nested
    class QuandoUsuarioListaTodasAsContas extends ListarTodasAsContasSetup {

        @Override
        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(403);
            assertThat(responseError.getMessage()).isEqualTo("Não tem permissão para acessar esse recurso.");
        }
    }
}