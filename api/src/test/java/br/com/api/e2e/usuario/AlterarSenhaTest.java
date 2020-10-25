package br.com.api.e2e.usuario;

import br.com.api.dto.AlterarSenhaDto;
import br.com.api.dto.HabilitarOrDesabilitarDoacaoDto;
import br.com.api.exception.FieldErrorMessage;
import br.com.api.model.Usuario;
import br.com.api.seed.UsuarioSetup;
import br.com.api.service.UsuarioService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlterarSenhaTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Nested
    abstract class AlterarSenhaSetup {
        ResponseEntity<String> responseEntity;
        Usuario usuario;
        ResponseError responseError;

        @BeforeEach
        void run() {
            usuarioSetup.setup();
            String login = getUserLogin();
            testUtil.login(port, login);
            String url = String.format("http://localhost:%s/usuarios", port);
            AlterarSenhaDto alterarSenhaDto = getAlterarSenhaDto();
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(alterarSenhaDto);
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 204) {
                usuario = usuarioService.buscarPorId(alterarSenhaDto.getIdUsuario());
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUserLogin() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        protected AlterarSenhaDto getAlterarSenhaDto() {
            return AlterarSenhaDto.builder()
                    .idUsuario(usuarioSetup.getUsuario1().getId())
                    .senhaAntiga("senha")
                    .senhaNova("senhanova")
                    .build();
        }
    }


    @Nested
    class QuandoUsuarioAtualizaSenhaComInformacoesValidas extends AlterarSenhaSetup {

        @Test
        void deveAtualizarSenha() {
            assertThat(bCryptPasswordEncoder.matches("senhanova", usuario.getSenha())).isTrue();
        }
    }

    @Nested
    class QuandoAdminAtualizaSenhaComInformacoesValidas extends AlterarSenhaSetup {

        @Override
        protected String getUserLogin() {
            return usuarioSetup.getAdmin().getEmail();
        }

        @Override
        protected AlterarSenhaDto getAlterarSenhaDto() {
            return super.getAlterarSenhaDto().withIdUsuario(usuarioSetup.getAdmin().getId());
        }

        @Test
        void deveAtualizarSenha() {
            assertThat(new BCryptPasswordEncoder().matches("senhanova", usuario.getSenha())).isTrue();
        }
    }


    @Nested
    class QuandoUsuarioAtualizaSenhaComInformacoesInvalidas extends AlterarSenhaSetup {

        @Override
        protected AlterarSenhaDto getAlterarSenhaDto() {
            return super.getAlterarSenhaDto().withSenhaAntiga("");
        }

        @Test
        void deveRetornarErroDeValidacaoCorrespondente() {
            assertThat(responseError.getErrors()).contains(new FieldErrorMessage("senhaAntiga", "Senha antiga deve ser preenchida."));
        }
    }

    @Nested
    class QuandoUsuarioAtualizaSenhaDeOutroUsuario extends AlterarSenhaSetup {


        @Override
        protected AlterarSenhaDto getAlterarSenhaDto() {
            return super.getAlterarSenhaDto().withIdUsuario(usuarioSetup.getUsuario2().getId());
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getMessage()).isEqualTo("Não pode atualizar senha dos outros usuários");
        }
    }
}
