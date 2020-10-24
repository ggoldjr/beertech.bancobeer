package br.com.api.service;

import br.com.api.dto.TransferenciaDto;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.model.Usuario;
import br.com.api.seed.ContaSetup;
import br.com.api.seed.UsuarioSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CriarTransferenciaTest {

    @Autowired
    OperacaoService operacaoService;

    @Autowired
    ContaService contaService;

    @Autowired
    ContaSetup contaSetup;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Nested
    abstract class CriarTransferenciaSetup {

        Operacao operacao;
        Conta contaOrigem;
        Conta contaDestino;


        @BeforeEach
        void run() throws ExecutionException, InterruptedException {
            contaSetup.setup();
            usuarioSetup.setup();
            setup();
            Usuario usuario = usuarioSetup.getUsuario1();
            operacao = operacaoService.criarTransferencia(getTransferenciaDto(), usuario);
            contaOrigem = contaService.findByHash(contaOrigem.getHash(), usuario);
            contaDestino =contaService.findByHash(contaDestino.getHash(), usuario);
        }

        abstract TransferenciaDto getTransferenciaDto();
        abstract void setup();
    }


    @Nested
    class CriarTransferenciaValida extends CriarTransferenciaSetup {

        @Override
        TransferenciaDto getTransferenciaDto() {
            return TransferenciaDto.builder()
                    .valor(100d)
                    .hashContaOrigem(contaOrigem.getHash())
                    .hashContaDestino(contaDestino.getHash())
                    .build();
        }

        @Override
        void setup() {
            contaOrigem = contaSetup.getContas().get(0);
            contaDestino = contaSetup.getContas().get(1);
        }

        @Test
        void deveRetornTipoOperacaoIgualATransferencia() {
            assertThat(operacao.getTipo()).isEqualTo(Operacao.Tipo.TRANSFERENCIA);
        }

        @Test
        void deveRetornarContaOrigemCom900() {
            assertThat(contaOrigem.getSaldo()).isEqualTo(900);
        }

        @Test
        void deveRetornarContaDestinoCom1100() {
            assertThat(contaDestino.getSaldo()).isEqualTo(1100);
        }
    }
}
