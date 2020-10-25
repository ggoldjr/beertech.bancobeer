package br.com.api.e2e.conta;

import br.com.api.dto.OperacaoDto;
import br.com.api.dto.UsuarioDto;
import br.com.api.exception.FieldErrorMessage;
import br.com.api.model.Conta;
import br.com.api.seed.UsuarioSetup;
import br.com.api.service.ContaService;
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
public class DepositoTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Autowired
    ContaService contaService;

    @Nested
    abstract class DepositoSetup {
        ResponseEntity<String> responseEntity;
        Conta conta;
        ResponseError responseError;
        String hash;

        @BeforeEach
        void run() {
            usuarioSetup.setup();
            testUtil.login(port, getUsuarioLogado());
            hash = getHash();
            String url = String.format("http://localhost:%s/contas/%s/operacoes/depositos", port, hash);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(getOperacaoDto());
            responseEntity = testUtil.restTemplate.postForEntity(url, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 201) {
                conta = contaService.findByHash(hash);
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        abstract String getHash();

        abstract OperacaoDto getOperacaoDto();

        public String getUsuarioLogado() {
            return usuarioSetup.getAdmin().getEmail();
        }
    }


    @Nested
    class CriarDepositoValido extends DepositoSetup {

        @Override
        String getHash() {
            return usuarioSetup.getUsuario1().getContaHash();
        }

        @Override
        OperacaoDto getOperacaoDto() {
            return OperacaoDto.builder()
                    .tipo("DEPOSITO")
                    .valor(BigDecimal.valueOf(100))
                    .build();
        }

        @Test
        void deveRetornarContaComSaldoAtualizado() {
            assertThat(conta.getSaldo()).isEqualTo(2100);
        }
    }


    @Nested
    class CriarDepositoInvalido extends DepositoSetup {

        @Override
        String getHash() {
            return usuarioSetup.getUsuario1().getContaHash();
        }

        @Override
        OperacaoDto getOperacaoDto() {
            return OperacaoDto.builder()
                    .valor(BigDecimal.valueOf(0))
                    .build();
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("valor", "Valor da operação deve ser maior que 0."));
        }
    }


    @Nested
    class QuandoUsuarioFazDeposito extends DepositoSetup {

        @Override
        String getHash() {
            return usuarioSetup.getUsuario2().getContaHash();
        }

        @Override
        public String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Override
        OperacaoDto getOperacaoDto() {
            return OperacaoDto.builder()
                    .tipo("DEPOSITO")
                    .valor(BigDecimal.valueOf(100))
                    .build();
        }


        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(403);
            assertThat(responseError.getMessage()).isEqualTo("Não tem permissão para acessar esse recurso.");
        }
    }


    @Nested
    class DepositarNumaContaInexistente extends DepositoSetup {

        @Override
        String getHash() {
            return "5a8a954d-eb1a-4010-85d2-a7e1d2495751";
        }

        @Override
        OperacaoDto getOperacaoDto() {
            return OperacaoDto.builder()
                    .tipo("DEPOSITO")
                    .valor(BigDecimal.valueOf(100))
                    .build();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(404);
            assertThat(responseError.getMessage()).isEqualTo("Conta com hash 5a8a954d-eb1a-4010-85d2-a7e1d2495751 não encontrado");
        }
    }
}
