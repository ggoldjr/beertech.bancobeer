package br.com.api.trasferencia;

import br.com.api.dto.TransferenciaDto;
import br.com.api.exception.FieldErrorMessage;
import br.com.api.model.Conta;
import br.com.api.model.Transferencia;
import br.com.api.seed.ContaSetup;
import br.com.api.service.ContaService;
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
public class CriarTransferenciaTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    ContaSetup contaSetup;

    @Autowired
    ContaService contaService;

    @Nested
    abstract class CriarTransferenciaSetup {
        ResponseEntity<String> responseEntity;
        Transferencia trasferenciaCriada;
        ResponseError responseError;
        Conta contaOrigem;
        Conta contaDestino;
        Conta contaOrigemAtualizada;
        Conta contaDestinoAtualizada;

        @BeforeEach
        void setup() throws JsonProcessingException {
            contaSetup.setup();
            contaOrigem = contaSetup.getContas().get(0);
            contaDestino = contaSetup.getContas().get(1);
            String url = String.format("http://localhost:%s/transferencias", port);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(getTransferenciaDto());
            responseEntity = testUtil.restTemplate.postForEntity(url, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                trasferenciaCriada = testUtil.parseSuccessfulResponse(responseEntity, Transferencia.class);
                contaOrigemAtualizada = contaService.findByHash(contaOrigem.getHash());
                contaDestinoAtualizada = contaService.findByHash(contaDestino.getHash());
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        abstract TransferenciaDto getTransferenciaDto();
    }

    @Nested
    class CriarTransferenciaValida extends CriarTransferenciaSetup {

        @Test
        void deveRetornarTransferenciaComId() {
            assertThat(trasferenciaCriada.getId()).isNotNull();
        }

        @Test
        void deveRetornaTransferenciaComHashDaContaDestino() {
            assertThat(trasferenciaCriada.getContaDestinoHash()).isEqualTo(contaDestino.getHash());
        }

        @Test
        void deveRetornaTransferenciaComContaOrigem() {
            assertThat(trasferenciaCriada.getContaOrigem().getHash()).isEqualTo(contaOrigem.getHash());
        }

        @Test
        void deveRetornaContaOrigemComSaldoIgualA900() {
            assertThat(contaOrigemAtualizada.getSaldo()).isEqualTo(900d);
        }

        @Test
        void deveRetornaContaDestinoComSaldoIgualA1100() {
            assertThat(contaDestinoAtualizada.getSaldo()).isEqualTo(1100d);
        }

        @Override
        TransferenciaDto getTransferenciaDto() {
            TransferenciaDto transferenciaDto = new TransferenciaDto();
            transferenciaDto.setHashContaOrigem(contaOrigem.getHash());
            transferenciaDto.setHashContaDestino(contaDestino.getHash());
            transferenciaDto.setValor(100d);
            return transferenciaDto;
        }
    }


    @Nested
    class TransferenciaComValorTotalDaConta extends CriarTransferenciaSetup {

        @Test
        void deveRetornarTransferenciaComId() {
            assertThat(trasferenciaCriada.getId()).isNotNull();
        }

        @Test
        void deveRetornaTransferenciaComHashDaContaDestino() {
            assertThat(trasferenciaCriada.getContaDestinoHash()).isEqualTo(contaDestino.getHash());
        }

        @Test
        void deveRetornaTransferenciaComContaOrigem() {
            assertThat(trasferenciaCriada.getContaOrigem().getHash()).isEqualTo(contaOrigem.getHash());
        }

        @Test
        void deveRetornaContaOrigemComSaldoIgualAZero() {
            assertThat(contaOrigemAtualizada.getSaldo()).isEqualTo(0);
        }

        @Test
        void deveRetornaContaDestinoComSaldoIgualA2000() {
            assertThat(contaDestinoAtualizada.getSaldo()).isEqualTo(2000);
        }

        @Override
        TransferenciaDto getTransferenciaDto() {
            TransferenciaDto transferenciaDto = new TransferenciaDto();
            transferenciaDto.setHashContaOrigem(contaOrigem.getHash());
            transferenciaDto.setHashContaDestino(contaDestino.getHash());
            transferenciaDto.setValor(1000d);
            return transferenciaDto;
        }
    }


    @Nested
    class CriarTransferenciaInvalida extends CriarTransferenciaSetup {


        @Override
        TransferenciaDto getTransferenciaDto() {
            TransferenciaDto transferenciaDto = new TransferenciaDto();
            transferenciaDto.setHashContaDestino(contaDestino.getHash());
            transferenciaDto.setValor(100d);
            return transferenciaDto;
        }

        @Test
        void deveRetornarMensagemDeErroCorrespondente() {
            assertThat(responseError.getMessage()).isEqualTo("Erro de validação");
            assertThat(responseError.getStatus()).isEqualTo(400);
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("hashContaOrigem",
                    "Deve ser diferente de nulo"));
        }
    }


    @Nested
    class CriarTransferenciaComValorNegativo extends CriarTransferenciaSetup {


        @Override
        TransferenciaDto getTransferenciaDto() {
            TransferenciaDto transferenciaDto = new TransferenciaDto();
            transferenciaDto.setHashContaOrigem(contaOrigem.getHash());
            transferenciaDto.setHashContaDestino(contaDestino.getHash());
            transferenciaDto.setValor(-100d);
            return transferenciaDto;
        }

        @Test
        void deveRetornarMensagemDeErroCorrespondente() {
            assertThat(responseError.getMessage()).isEqualTo("Erro de validação");
            assertThat(responseError.getStatus()).isEqualTo(400);
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("valor",
                    "Valor deve ser maior que zero"));
        }
    }


    @Nested
    class CriarTransferenciaComValorZero extends CriarTransferenciaSetup {


        @Override
        TransferenciaDto getTransferenciaDto() {
            TransferenciaDto transferenciaDto = new TransferenciaDto();
            transferenciaDto.setHashContaOrigem(contaOrigem.getHash());
            transferenciaDto.setHashContaDestino(contaDestino.getHash());
            transferenciaDto.setValor(0d);
            return transferenciaDto;
        }

        @Test
        void deveRetornarMensagemDeErroCorrespondente() {
            assertThat(responseError.getMessage()).isEqualTo("Erro de validação");
            assertThat(responseError.getStatus()).isEqualTo(400);
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("valor",
                    "Valor deve ser maior que zero"));
        }
    }
}
