package br.com.api.e2e.usuario;

import br.com.api.dto.UsuarioDto;
import br.com.api.exception.FieldErrorMessage;
import br.com.api.model.Usuario;
import br.com.api.seed.UsuarioSetup;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CriarUsuarioTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;


    @Nested
    abstract class CriarUsuarioSetup {
        ResponseEntity<String> responseEntity;
        UsuarioDto usuarioDto;
        ResponseError responseError;

        @BeforeEach
        void run() {
            String url = String.format("http://localhost:%s/usuarios", port);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(getUsuarioSpec());
            setup();
            responseEntity = testUtil.restTemplate.postForEntity(url, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 201) {
                usuarioDto = testUtil.parseSuccessfulResponse(responseEntity, UsuarioDto.class);
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }
        abstract UsuarioSpec getUsuarioSpec();
        void setup(){ usuarioSetup.deleteAll();}
    }


    @Nested
    class CriarUsuarioValido extends CriarUsuarioSetup {

        @Override
        UsuarioSpec getUsuarioSpec() {
            return UsuarioSpec.builder()
                    .cnpj("14072384000162")
                    .email("teste@gmail.com")
                    .nome("teste")
                    .senha("senha")
                    .build();
        }

        @Test
        void deveRetornarUsuarioCriado() {
            assertThat(usuarioDto.getId()).isNotNull();
            assertThat(usuarioDto.getCnpj()).isEqualTo("14072384000162");
            assertThat(usuarioDto.getEmail()).isEqualTo("teste@gmail.com");
            assertThat(usuarioDto.getNome()).isEqualTo("teste");
            assertThat(usuarioDto.getPerfil()).isEqualTo(Usuario.Perfil.USUARIO);
            assertThat(usuarioDto.getContaDto().getHash()).isNotNull();
            assertThat(usuarioDto.getContaDto().getId()).isNotNull();
            assertThat(usuarioDto.getContaDto().getSaldo()).isEqualTo(0);
        }
    }


    @Nested
    class CriarUsuarioSemNome extends CriarUsuarioSetup {

        @Override
        UsuarioSpec getUsuarioSpec() {
            return UsuarioSpec.builder()
                    .cnpj("14072384000162")
                    .email("teste@gmail.com")
                    .senha("senha")
                    .build();
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("nome", "Nome não pode ser nulo."));
        }
    }


    @Nested
    class CriarUsuarioComCNPJDuplicado extends CriarUsuarioSetup {

        @Override
        UsuarioSpec getUsuarioSpec() {
            return UsuarioSpec.builder()
                    .nome("Usuario teste 1")
                    .email("teste12@gmail.com")
                    .senha("senha")
                    .cnpj("82826677000148")
                    .build();
        }

        @Override
        void setup() {
            super.setup();
            usuarioSetup.setup();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getMessage()).isEqualTo("Já existe recurso com '82826677000148'.");
            assertThat(responseError.getStatus()).isEqualTo(409);
        }
    }

    @Nested
    class CriarUsuarioComEmailDuplicado extends CriarUsuarioSetup {

        @Override
        UsuarioSpec getUsuarioSpec() {
            return UsuarioSpec.builder()
                    .nome("Usuario teste 1")
                    .email("teste1@gmail.com")
                    .senha("senha")
                    .cnpj("14072384000162")
                    .build();
        }

        @Override
        void setup() {
            super.setup();
            usuarioSetup.setup();
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getMessage()).isEqualTo("Já existe recurso com 'teste1@gmail.com'.");
            assertThat(responseError.getStatus()).isEqualTo(409);
        }
    }
}
