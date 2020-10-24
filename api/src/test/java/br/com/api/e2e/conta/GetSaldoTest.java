package br.com.api.e2e.conta;

import br.com.api.dto.OperacaoDto;
import br.com.api.dto.SaldoDto;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.model.Usuario;
import br.com.api.seed.UsuarioSetup;
import br.com.api.service.ContaService;
import br.com.api.service.OperacaoService;
import br.com.api.util.ResponseError;
import br.com.api.util.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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
    ContaService contaService;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Autowired
    OperacaoService operacaoService;

    @Nested
    public abstract class SetupGetSaldo {
        ResponseEntity<String> responseEntity;
        String contaHash;
        ResponseError responseError;
        Conta conta;
        SaldoDto saldoDto;
        Usuario usuario1;

        @BeforeEach
        void setup() throws JsonProcessingException {
            usuarioSetup.setup();
            usuario1 = usuarioSetup.getUsuario1();
            testUtil.login(port, usuario1.getEmail());
            setContaHash();
            String url = String.format("http://localhost:%s/contas/%s/saldos", port, contaHash);
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.GET, testUtil.getHttpEntity(), String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                saldoDto = testUtil.parseSuccessfulResponse(responseEntity, SaldoDto.class);
                conta = contaService.findByHash(contaHash);
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        void setContaHash() {
            contaHash = usuario1.getContaHash();
        }

        protected void setupConta() {
            OperacaoDto operacaoDto = new OperacaoDto();
            operacaoDto.setTipo(Operacao.Tipo.SAQUE.name());
            operacaoDto.setValor(100D);

            OperacaoDto operacaoDto1 = new OperacaoDto();
            operacaoDto1.setTipo(Operacao.Tipo.DEPOSITO.name());
            operacaoDto1.setValor(50d);

            operacaoService.criar(usuario1.getContaHash(), operacaoDto);
            operacaoService.criar(usuario1.getContaHash(), operacaoDto1);
        }
    }


    @Nested
    public class BuscarSaldoDaContaExistente extends SetupGetSaldo {

        @Test
        void DeveRetornarSadoIgualA950() {
            assertThat(saldoDto.getSaldo()).isEqualTo(950);
        }
    }

    @Nested
    public class BuscarSaldoDaContaInexistente extends SetupGetSaldo {

        @Override
        void setContaHash() {
            contaHash = "48394374938493434839849343";
        }

        @Test
        void deveRetornarMensagemDeErroCorrespondente() {
            assertThat(responseError.getMessage()).isEqualTo("Conta com hash 48394374938493434839849343 não encontrado");
            assertThat(responseError.getStatus()).isEqualTo(404);
        }
    }
}