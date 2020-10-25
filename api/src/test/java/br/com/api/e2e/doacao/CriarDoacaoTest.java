package br.com.api.e2e.doacao;

import br.com.api.dto.DoacaoDto;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.model.Usuario;
import br.com.api.seed.UsuarioSetup;
import br.com.api.service.ContaService;
import br.com.api.service.OperacaoService;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CriarDoacaoTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Autowired
    ContaService contaService;

    @Autowired
    OperacaoService operacaoService;


    @Nested
    abstract class CriarDoacaoSetup {
        ResponseEntity<String> responseEntity;
        ResponseError responseError;
        Operacao operacao;
        Conta contaDestino;
        Conta contaOrigem;

        @BeforeEach
        void run() {
            usuarioSetup.setup();
            usuarioSetup.criarUsuario(2);
            String usuarioLogado = getUsuarioLogado();
            testUtil.login(port, usuarioLogado);
            String url = String.format("http://localhost:%s/doacoes", port);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(getDoacaoDto());
            responseEntity = testUtil.restTemplate.postForEntity(url, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 201) {
                operacao = operacaoService.findAllByContaHash(usuarioSetup.getUsuario1().getContaHash()).stream().filter(operacao1 -> operacao1.getTipo() == Operacao.Tipo.DOACAO).findFirst().get();
                contaOrigem = contaService.findByHash(operacao.getConta().getHash());
                contaDestino = contaService.findByHash(operacao.getHashContaDestino());
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUsuarioLogado() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        abstract DoacaoDto getDoacaoDto();
    }


    @Nested
    class QuandoFazDoacao extends CriarDoacaoSetup {

        @Override
        DoacaoDto getDoacaoDto() {
            return DoacaoDto.builder()
                    .valorDoado(BigDecimal.valueOf(100))
                    .idUsuarioBeneficiario(usuarioSetup.getUsuarios().get(1).getId())
                    .build();
        }

        @Test
        void deveRetornarOperacaoCriada() {
            assertThat(operacao.getValor().doubleValue()).isEqualTo(100);
            assertThat(operacao.getTipo()).isEqualTo(Operacao.Tipo.DOACAO);
            assertThat(operacao.getConta().getHash()).isEqualTo(usuarioSetup.getUsuario1().getContaHash());
            assertThat(operacao.getHashContaDestino()).isEqualTo(usuarioSetup.getUsuarios().get(1).getContaHash());
            assertThat(contaOrigem.getSaldo()).isEqualTo(1900);
            assertThat(contaDestino.getSaldo()).isEqualTo(100);
        }
    }
}
