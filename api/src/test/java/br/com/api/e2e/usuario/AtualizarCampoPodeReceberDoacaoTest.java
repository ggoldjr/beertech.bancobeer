package br.com.api.e2e.usuario;


import br.com.api.dto.HabilitarOrDesabilitarDoacaoDto;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AtualizarCampoPodeReceberDoacaoTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Autowired
    UsuarioService usuarioService;


    @Nested
    abstract class AtualizarCampoPodeReceberDoacaoSetup {
        ResponseEntity<String> responseEntity;
        Usuario usuario;
        ResponseError responseError;

        @BeforeEach
        void run() {
            usuarioSetup.setup();
            testUtil.login(port, getUserLogin());
            String url = String.format("http://localhost:%s/usuarios/doacoes", port);
            HabilitarOrDesabilitarDoacaoDto habilitarOrDesabilitarDoacaoDto = getHabilitarOrDesabilitarDoacaoDto();
            HttpEntity<String> httpEntity = testUtil.getHttpEntity(habilitarOrDesabilitarDoacaoDto);
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 204) {
                usuario = usuarioService.buscarPorId(habilitarOrDesabilitarDoacaoDto.getIdUsuario());
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUserLogin() {
            return usuarioSetup.getAdmin().getEmail();
        }

        protected HabilitarOrDesabilitarDoacaoDto getHabilitarOrDesabilitarDoacaoDto() {
            return HabilitarOrDesabilitarDoacaoDto.builder()
                    .idUsuario(usuarioSetup.getUsuario1().getId())
                    .podeReceberDoacao(true)
                    .build();
        }
    }


    @Nested
    class QuandoAdminAtualizaUsuarioParaReceberDoacoes extends AtualizarCampoPodeReceberDoacaoSetup {

        @Test
        void deveRetornarUsuarioAtualizado() {
            assertThat(usuario.getPodeReceberDoacoes()).isTrue();
        }
    }


    @Nested
    class QuandoAdminAtualizaEleMesmoParaReceberDoacoes extends AtualizarCampoPodeReceberDoacaoSetup {

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(401);
            assertThat(responseError.getMessage()).isEqualTo("Só pode atualizar usuários com perfil de USUARIO");
        }

        protected HabilitarOrDesabilitarDoacaoDto getHabilitarOrDesabilitarDoacaoDto() {
            return HabilitarOrDesabilitarDoacaoDto.builder()
                    .idUsuario(usuarioSetup.getAdmin().getId())
                    .podeReceberDoacao(true)
                    .build();
        }
    }


    @Nested
    class QuandoUsuarioAtualizaOutroUsuarioParaReceberDoacoes extends AtualizarCampoPodeReceberDoacaoSetup {

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(403);
            assertThat(responseError.getMessage()).isEqualTo("Não tem permissão para acessar esse recurso.");
        }

        @Override
        protected String getUserLogin() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        protected HabilitarOrDesabilitarDoacaoDto getHabilitarOrDesabilitarDoacaoDto() {
            return HabilitarOrDesabilitarDoacaoDto.builder()
                    .idUsuario(usuarioSetup.getUsuario2().getId())
                    .podeReceberDoacao(true)
                    .build();
        }
    }
}