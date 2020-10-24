package br.com.api.e2e.conta;

import br.com.api.dto.OperacaoDto;
import br.com.api.exception.FieldErrorMessage;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.seed.ContaSetup;
import br.com.api.seed.UsuarioSetup;
import br.com.api.service.ContaService;
import br.com.api.service.OperacaoService;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CriarOperacaoTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    ContaSetup contaSetup;

    @Autowired
    ContaService contaService;

    @Autowired
    OperacaoService operacaoService;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Nested
    abstract class CriarOperacaoSetup {
        ResponseEntity<String> responseEntity;
        Operacao operacaoCriada;
        ResponseError responseError;
        String contaHash;
        Conta conta;

        @BeforeEach
        void setup() throws JsonProcessingException, InterruptedException {
            usuarioSetup.setup();
            testUtil.login(port, usuarioSetup.getAdmin().getEmail());
            contaHash = usuarioSetup.getUsuario1().getContaHash();
            String url = String.format("http://localhost:%s/contas/%s/operacoes/%s", port, contaHash, getOperacao());
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(getOperacaoDto());
            responseEntity = testUtil.restTemplate.postForEntity(url, httpEntity, String.class);
            Thread.sleep(3000);
            if (responseEntity.getStatusCodeValue() == 201) {
                List<Operacao> operacoes = operacaoService.getOperacaoDaConta(contaHash);
                assertThat(operacoes.size()).isEqualTo(2);
                operacaoCriada = operacoes.get(1);
                conta = contaService.findByHash(contaHash);
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }
        abstract OperacaoDto getOperacaoDto();
        abstract String getOperacao();
    }


    @Nested
    public class SacarValorMenorQueSaldo extends CriarOperacaoSetup {

        @Test
        void deveRetornarContaComSaldoIgualA900() {
            assertThat(conta.getSaldo()).isEqualTo(900);
        }

        @Test
        void deveCriaOperacao() {
            assertThat(operacaoCriada).isNotNull();
            assertThat(operacaoCriada.getTipo()).isEqualTo(Operacao.Tipo.SAQUE);
            assertThat(operacaoCriada.getValor()).isEqualTo(100);
            assertThat(operacaoCriada.getDataOperacao().truncatedTo(ChronoUnit.HOURS)).isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        }

        @Test
        void deveAtualizarSaldoDaContaPara900() {
            assertThat(conta.getSaldo()).isEqualTo(900);
        }

        @Override
        OperacaoDto getOperacaoDto() {
            OperacaoDto operacaoDto = new OperacaoDto();
            operacaoDto.setTipo(Operacao.Tipo.SAQUE.name());
            operacaoDto.setValor(100D);
            return operacaoDto;
        }

        @Override
        String getOperacao() {
            return "saques";
        }
    }

    @Nested
    public class SacarValorMaiorQueSaldo extends CriarOperacaoSetup {

        @Override
        OperacaoDto getOperacaoDto() {
            OperacaoDto operacaoDto = new OperacaoDto();
            operacaoDto.setTipo(Operacao.Tipo.SAQUE.name());
            operacaoDto.setValor(2000D);
            return operacaoDto;
        }

        @Test
        @Disabled
        void deveRetornarMensagemDeErroCorrespondente() {
            assertThat(responseError.getMessage()).isEqualTo("Saldo Insuficiente");
            assertThat(responseError.getStatus()).isEqualTo(400);
        }

        @Override
        String getOperacao() {
            return "saques";
        }

    }

    @Nested
    public class RealizarOperacaoInvalida extends CriarOperacaoSetup {

        @Override
        OperacaoDto getOperacaoDto() {
            OperacaoDto operacaoDto = new OperacaoDto();
            operacaoDto.setValor(-10d);
            return operacaoDto;
        }

        @Test
        void itShouldReturnCorrectMessage() {
            assertThat(responseError.getMessage()).isEqualTo("Erro de validação");
            assertThat(responseError.getStatus()).isEqualTo(400);
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("valor",
                    "Valor deve ser maior que zero"));
        }

        @Override
        String getOperacao() {
            return "saques";
        }

    }

    @Nested
    public class DepositorValorPositivo extends CriarOperacaoSetup {

        @Test
        void deveRetornarContaComSaldoIgualA2000() {
            assertThat(conta.getSaldo()).isEqualTo(2000);
        }

        @Test
        void deveCriaTransacao() {
            assertThat(operacaoCriada).isNotNull();
            assertThat(operacaoCriada.getTipo()).isEqualTo(Operacao.Tipo.DEPOSITO);
            assertThat(operacaoCriada.getValor()).isEqualTo(1000);
            assertThat(operacaoCriada.getDataOperacao().truncatedTo(ChronoUnit.HOURS)).isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
        }

        @Test
        void deveAtualizarSaldoDaContaPara2000() {
            assertThat(conta.getSaldo()).isEqualTo(2000);
        }

        @Override
        OperacaoDto getOperacaoDto() {
            OperacaoDto operacaoDto = new OperacaoDto();
            operacaoDto.setTipo(Operacao.Tipo.DEPOSITO.name());
            operacaoDto.setValor(1000D);
            return operacaoDto;
        }

        @Override
        String getOperacao() {
            return "depositos";
        }
    }

    @Nested
    public class DepositarValorNegativo extends CriarOperacaoSetup {

        @Override
        OperacaoDto getOperacaoDto() {
            OperacaoDto operacaoDto = new OperacaoDto();
            operacaoDto.setTipo(Operacao.Tipo.DEPOSITO.name());
            operacaoDto.setValor(-500D);
            return operacaoDto;
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

        @Override
        String getOperacao() {
            return "depositos";
        }
    }


    @Nested
    public class DepositarValorZero extends CriarOperacaoSetup {

        @Override
        OperacaoDto getOperacaoDto() {
            OperacaoDto operacaoDto = new OperacaoDto();
            operacaoDto.setTipo(Operacao.Tipo.DEPOSITO.name());
            operacaoDto.setValor(0d);
            return operacaoDto;
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

        @Override
        String getOperacao() {
            return "depositos";
        }
    }
}