package br.com.api.e2e.usuario;

import br.com.api.dto.HabilitarOrDesabilitarDoacaoDto;
import br.com.api.dto.UsuarioDto;
import br.com.api.exception.FieldErrorMessage;
import br.com.api.model.Usuario;
import br.com.api.seed.UsuarioSetup;
import br.com.api.service.UsuarioService;
import br.com.api.spec.AtualizarUsuarioSpec;
import br.com.api.util.ResponseError;
import br.com.api.util.TestUtil;
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
public class AtualizarUsuarioTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Autowired
    UsuarioService usuarioService;


    @Nested
    abstract class AtualizarUsuarioSetup {
        ResponseEntity<String> responseEntity;
        UsuarioDto usuario;
        ResponseError responseError;

        @BeforeEach
        void run() {
            usuarioSetup.setup();
            testUtil.login(port, getUserLogin());
            String url = String.format("http://localhost:%s/usuarios", port);
            AtualizarUsuarioSpec atualizarUsuarioSpec = getAtualizarUsuarioSpec();
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(atualizarUsuarioSpec);
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                usuario = testUtil.parseSuccessfulResponse(responseEntity, UsuarioDto.class);
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUserLogin() {
            return usuarioSetup.getAdmin().getEmail();
        }

        protected AtualizarUsuarioSpec getAtualizarUsuarioSpec() {
            return AtualizarUsuarioSpec.builder()
                    .nome("nome atualizado")
                    .email("emailatualizado@gmail.com")
                    .cnpj("65648326000175")
                    .build();
        }
    }


    @Nested
    class AtualizarUsuarioComDadosValidos extends AtualizarUsuarioSetup {

        @Test
        void deveAtualizarUsuario() {
            assertThat(usuario.getNome()).isEqualTo("nome atualizado");
            assertThat(usuario.getEmail()).isEqualTo("emailatualizado@gmail.com");
            assertThat(usuario.getCnpj()).isEqualTo("65648326000175");
        }
    }


    @Nested
    class AtualizarUsuarioComDadosInvalidos extends AtualizarUsuarioSetup {

        @Override
        protected AtualizarUsuarioSpec getAtualizarUsuarioSpec() {
            return AtualizarUsuarioSpec.builder()
                    .nome(null)
                    .email("emailatualizado@gmail.com")
                    .cnpj("65648326000175")
                    .build();
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("nome", "Nome não pode ser nulo."));
        }
    }
}
