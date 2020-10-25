package br.com.api.e2e.usuario;

import br.com.api.dto.UsuarioDto;
import br.com.api.seed.UsuarioSetup;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListarTodosUsuariosTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;


    @Nested
    abstract class ListarTodosUsuariosSetup {
        ResponseEntity<String> responseEntity;
        List<UsuarioDto> usuarios;
        ResponseError responseError;

        @BeforeEach
        void run() throws JsonProcessingException {
            usuarioSetup.setup();
            testUtil.login(port, getUserLogin());
            String url = String.format("http://localhost:%s/usuarios/all", port);
            HttpEntity<String> httpEntity = testUtil.getHttpEntity();
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                usuarios = testUtil.mapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUserLogin() {
            return usuarioSetup.getAdmin().getEmail();
        }
    }


    @Nested
    class QaundoAdminListaTodosUsuarios extends ListarTodosUsuariosSetup {

        @Test
        void deveRetornar2Usuarios() {
            assertThat(usuarios.size()).isEqualTo(2);
        }

        @Test
        void naoDeveRetornarAdmin() {
            assertThat(usuarios.stream().anyMatch(usuarioDto -> usuarioDto.getId().longValue() == usuarioSetup.getAdmin().getId().longValue())).isFalse();
        }
    }


    @Nested
    class QaundoUsarioListaTodosUsuarios extends ListarTodosUsuariosSetup {

        @Override
        protected String getUserLogin() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        @Test
        void deveRetornar1Usuarios() {
            assertThat(usuarios.size()).isEqualTo(1);
        }

        @Test
        void naoDeveRetornarAdmin() {
            assertThat(usuarios.stream().anyMatch(usuarioDto -> usuarioDto.getId().longValue() == usuarioSetup.getUsuario1().getId().longValue())).isFalse();
        }
    }
}
