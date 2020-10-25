package br.com.api.e2e.conta;

import br.com.api.dto.TransferenciaDto;
import br.com.api.dto.UsuarioDto;
import br.com.api.exception.FieldErrorMessage;
import br.com.api.model.Conta;
import br.com.api.model.Usuario;
import br.com.api.seed.UsuarioSetup;
import br.com.api.service.ContaService;
import br.com.api.spec.UsuarioSpec;
import br.com.api.util.ResponseError;
import br.com.api.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CriarTransferenciaTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Autowired
    ContaService contaService;


    @Nested
    abstract class CriarTransferenciaSetup {
        ResponseEntity<String> responseEntity;
        ResponseError responseError;
        Conta contaOrigem;
        Conta contaDestino;
        Usuario usuarioOrigem;
        Usuario usuarioDestino;

        @BeforeEach
        void run() {
            usuarioSetup.setup();
            testUtil.login(port, getUsuarioLogado());
            setup();
            String url = String.format("http://localhost:%s/contas/operacoes/tranferencias", port);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(getTransferenciaDto());
            responseEntity = testUtil.restTemplate.postForEntity(url, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 201) {
                contaOrigem = contaService.findByHash(usuarioOrigem.getContaHash());
                contaDestino = contaService.findByHash(usuarioDestino.getContaHash());
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        abstract TransferenciaDto getTransferenciaDto();

        public void setup() {
            usuarioOrigem = usuarioSetup.getUsuario1();
            usuarioDestino = usuarioSetup.getUsuario2();
        }
    }


    @Nested
    class QuandoUsuarioCriaTransferenciaValida extends CriarTransferenciaSetup {

        @Override
        TransferenciaDto getTransferenciaDto() {
            return TransferenciaDto.builder()
                    .valor(BigDecimal.valueOf(100))
                    .hashContaOrigem(usuarioOrigem.getContaHash())
                    .hashContaDestino(usuarioDestino.getContaHash())
                    .build();
        }

        @Test
        void deveRetornarContasComSaldoAtualizado() {
            assertThat(contaOrigem.getSaldo()).isEqualTo(1900);
            assertThat(contaDestino.getSaldo()).isEqualTo(2100);
        }
    }


    @Nested
    class QuandoUsuarioCriaTransferenciaParaEleMesmo extends CriarTransferenciaSetup {

        @Override
        TransferenciaDto getTransferenciaDto() {
            return TransferenciaDto.builder()
                    .valor(BigDecimal.valueOf(100))
                    .hashContaOrigem(usuarioOrigem.getContaHash())
                    .hashContaDestino(usuarioOrigem.getContaHash())
                    .build();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(401);
            assertThat(responseError.getMessage()).isEqualTo("Não pode transferir dinheiro para você mesmo");
        }
    }


    @Nested
    class QuandoUsuarioCriaTransferenciaUsandoContaDeOutroUsuarioComoContaOrigem extends CriarTransferenciaSetup {

        @Override
        TransferenciaDto getTransferenciaDto() {
            return TransferenciaDto.builder()
                    .valor(BigDecimal.valueOf(100))
                    .hashContaOrigem(usuarioSetup.getUsuario2().getContaHash())
                    .hashContaDestino(usuarioOrigem.getContaHash())
                    .build();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(401);
            assertThat(responseError.getMessage()).isEqualTo("Só pode transferir dinheiro da sua conta");
        }
    }


    @Nested
    class QuandoUsuarioCriaTransferenciaComValorMairoQueSaldo extends CriarTransferenciaSetup {

        @Override
        TransferenciaDto getTransferenciaDto() {
            return TransferenciaDto.builder()
                    .valor(BigDecimal.valueOf(2100))
                    .hashContaOrigem(usuarioOrigem.getContaHash())
                    .hashContaDestino(usuarioDestino.getContaHash())
                    .build();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(400);
            assertThat(responseError.getMessage()).isEqualTo("Saldo Insuficiente");
        }
    }


    @Nested
    class QuandoUsuarioCriaTransferenciaComValorNegativo extends CriarTransferenciaSetup {

        @Override
        TransferenciaDto getTransferenciaDto() {
            return TransferenciaDto.builder()
                    .valor(BigDecimal.valueOf(-100))
                    .hashContaOrigem(usuarioOrigem.getContaHash())
                    .hashContaDestino(usuarioDestino.getContaHash())
                    .build();
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("valor", "Valor da transferência deve ser maior que zero."));
        }
    }


    @Nested
    class QuandoUsuarioCriaTransferenciaParaContaInexistente extends CriarTransferenciaSetup {

        @Override
        TransferenciaDto getTransferenciaDto() {
            return TransferenciaDto.builder()
                    .valor(BigDecimal.valueOf(100))
                    .hashContaOrigem(usuarioOrigem.getContaHash())
                    .hashContaDestino("3df196c5-36ed-4d28-a59b-2bffe9329c90")
                    .build();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(404);
            assertThat(responseError.getMessage()).isEqualTo("Conta com hash 3df196c5-36ed-4d28-a59b-2bffe9329c90 não encontrado");
        }
    }
}
