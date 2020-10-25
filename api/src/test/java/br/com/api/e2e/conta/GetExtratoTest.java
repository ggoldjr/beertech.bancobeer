package br.com.api.e2e.conta;


import br.com.api.dto.ExtratoDto;
import br.com.api.dto.OperacaoDto;
import br.com.api.dto.SaldoDto;
import br.com.api.dto.TransferenciaDto;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.model.Usuario;
import br.com.api.seed.UsuarioSetup;
import br.com.api.service.ContaService;
import br.com.api.service.OperacaoService;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetExtratoTest {

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
    public abstract class GetExtratoSetup {
        ResponseEntity<String> responseEntity;
        ResponseError responseError;
        List<ExtratoDto> extratos;
        Usuario usuario1;
        Usuario usuario2;

        @BeforeEach
        void run() throws JsonProcessingException {
            usuarioSetup.setup();
            usuario1 = usuarioSetup.getUsuario1();
            usuario2 = usuarioSetup.getUsuario2();
            testUtil.login(port, usuario1.getEmail());
            setup();
            String url = String.format("http://localhost:%s/contas/%s/extratos", port, usuario1.getContaHash());
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.GET, testUtil.getHttpEntity(), String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                extratos = testUtil.mapper.readValue(responseEntity.getBody(), new TypeReference<>(){});
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected void setup() {
            TransferenciaDto transferenciaDto = TransferenciaDto.builder()
                    .hashContaDestino(usuario2.getContaHash())
                    .hashContaOrigem(usuario1.getContaHash())
                    .valor(BigDecimal.valueOf(100))
                    .build();
            operacaoService.criarTransferencia(transferenciaDto, usuario1);
        }
    }


    @Nested
    class GetExtrato extends GetExtratoSetup {

        @Test
        void deveRetornarApenasExtratoDoUsuarioLogado() {
            assertThat(extratos.size()).isEqualTo(2);
        }
    }
}
