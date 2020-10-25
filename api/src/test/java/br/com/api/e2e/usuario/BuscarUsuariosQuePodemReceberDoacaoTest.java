package br.com.api.e2e.usuario;

import br.com.api.dto.DoacaoDto;
import br.com.api.dto.UsuarioDto;
import br.com.api.model.Usuario;
import br.com.api.seed.UsuarioSetup;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BuscarUsuariosQuePodemReceberDoacaoTest {

    @LocalServerPort
    private int port;

    @Autowired
    TestUtil testUtil;

    @Autowired
    UsuarioSetup usuarioSetup;

    @Autowired
    OperacaoService operacaoService;


    @Nested
    abstract class BuscarUsuariosQuePodemReceberDoacaoSetup {
        ResponseEntity<String> responseEntity;
        List<UsuarioDto> usuarios;
        ResponseError responseError;

        @BeforeEach
        void run() throws JsonProcessingException {
            usuarioSetup.setup();
            usuarioSetup.criarUsuario(10);
            setup();
            testUtil.login(port, getUserLogin());
            String url = String.format("http://localhost:%s/usuarios?%s", port, getQueryParams());
            HttpEntity<String> httpEntity = testUtil.getHttpEntity();
            responseEntity = testUtil.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
            if (responseEntity.getStatusCodeValue() == 200) {
                usuarios = testUtil.mapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
            } else {
                responseError = testUtil.parseResponseError(responseEntity);
            }
        }

        protected String getUserLogin() {
            return usuarioSetup.getUsuario1().getEmail();
        }

        protected abstract String getQueryParams();

        public void setup() {}
    }


    @Nested
    class ListarUsuariosQuePodemReceberDoacoes extends BuscarUsuariosQuePodemReceberDoacaoSetup {

        @Override
        protected String getQueryParams() {
            return "podeReceberDoacao=sim";
        }

        @Test
        void deveRetornar5Usuarios() {
            assertThat(usuarios.size()).isEqualTo(5);
        }

        @Test
        void deveRetornar5UsuariosComCampoPodeReceberDoacoesIgualAtrue() {
            assertThat(usuarios.stream().allMatch(UsuarioDto::getPodeReceberDoacoes)).isTrue();
        }
    }


    @Nested
    class QuandoAdminListaUsuariosQuePodemReceberDoacoes extends BuscarUsuariosQuePodemReceberDoacaoSetup {

        @Override
        protected String getUserLogin() {
            return usuarioSetup.getAdmin().getEmail();
        }

        @Override
        protected String getQueryParams() {
            return "podeReceberDoacao=sim";
        }

        @Test
        void deveRetornarMensagemDeErro() {
            assertThat(responseError.getStatus()).isEqualTo(403);
            assertThat(responseError.getMessage()).isEqualTo("Não tem permissão para acessar esse recurso.");
        }
    }

    @Nested
    class ListarUsuariosQueJaReceberamMinhaDoacao extends BuscarUsuariosQuePodemReceberDoacaoSetup {

        List<String> emails = new ArrayList<>();

        @Override
        public void setup() {
            List<Usuario> usuariosQuePodeReceberDoacao = usuarioSetup.getUsuariosQuePodeReceberDoacao();
            DoacaoDto doacaoDto1 = DoacaoDto.builder()
                    .idUsuarioBeneficiario(usuariosQuePodeReceberDoacao.get(0).getId())
                    .valorDoado(BigDecimal.valueOf(100))
                    .build();
            DoacaoDto doacaoDto2 = DoacaoDto.builder()
                    .idUsuarioBeneficiario(usuariosQuePodeReceberDoacao.get(1).getId())
                    .valorDoado(BigDecimal.valueOf(100))
                    .build();
            operacaoService.criarDoacao(usuarioSetup.getUsuario1(), doacaoDto1);
            operacaoService.criarDoacao(usuarioSetup.getUsuario1(), doacaoDto2);
            emails.add(usuariosQuePodeReceberDoacao.get(0).getEmail());
            emails.add(usuariosQuePodeReceberDoacao.get(1).getEmail());
        }

        @Override
        protected String getQueryParams() {
            return "minhasDoacoes=sim";
        }

        @Test
        void deveRetornar2Usuarios() {
            assertThat(usuarios.size()).isEqualTo(2);
        }

        @Test
        void deveRetornarUsuariosComCampoPodeReceberDoacoesIgualAtrue() {
            assertThat(usuarios.stream().allMatch(UsuarioDto::getPodeReceberDoacoes)).isTrue();
        }

        @Test
        void deveRetornarApenasUsuariosQueReceberamMinhasDoacoes() {
            usuarios.forEach(usuarioDto -> assertThat(emails.contains(usuarioDto.getEmail())).isTrue());
        }
    }


    @Nested
    class ListarUsuariosQueJaReceberamMinhaDoacaoEQuePodeAindaReceberDoacao extends BuscarUsuariosQuePodemReceberDoacaoSetup {

        List<String> emails = new ArrayList<>();

        @Override
        public void setup() {
            List<Usuario> usuariosQuePodeReceberDoacao = usuarioSetup.getUsuariosQuePodeReceberDoacao();
            DoacaoDto doacaoDto1 = DoacaoDto.builder()
                    .idUsuarioBeneficiario(usuariosQuePodeReceberDoacao.get(0).getId())
                    .valorDoado(BigDecimal.valueOf(100))
                    .build();
            DoacaoDto doacaoDto2 = DoacaoDto.builder()
                    .idUsuarioBeneficiario(usuariosQuePodeReceberDoacao.get(1).getId())
                    .valorDoado(BigDecimal.valueOf(1000))
                    .build();
            operacaoService.criarDoacao(usuarioSetup.getUsuario1(), doacaoDto1);
            operacaoService.criarDoacao(usuarioSetup.getUsuario1(), doacaoDto2);
            emails.add(usuariosQuePodeReceberDoacao.get(0).getEmail());
        }

        @Override
        protected String getQueryParams() {
            return "minhasDoacoes=sim&podeReceberDoacao=sim";
        }

        @Test
        void deveRetornar1Usuarios() {
            assertThat(usuarios.size()).isEqualTo(1);
        }

        @Test
        void deveRetornarUsuariosComCampoPodeReceberDoacoesIgualAtrue() {
            assertThat(usuarios.stream().allMatch(UsuarioDto::getPodeReceberDoacoes)).isTrue();
        }

        @Test
        void deveRetornarApenasUsuariosQueReceberamMinhasDoacoes() {
            usuarios.forEach(usuarioDto -> assertThat(emails.contains(usuarioDto.getEmail())).isTrue());
        }
    }



    @Nested
    class ListarUsuariosSemDoacao extends BuscarUsuariosQuePodemReceberDoacaoSetup {

        @Override
        public void setup() {
            List<Usuario> usuariosQuePodeReceberDoacao = usuarioSetup.getUsuariosQuePodeReceberDoacao();
            DoacaoDto doacaoDto1 = DoacaoDto.builder()
                    .idUsuarioBeneficiario(usuariosQuePodeReceberDoacao.get(0).getId())
                    .valorDoado(BigDecimal.valueOf(100))
                    .build();
            DoacaoDto doacaoDto2 = DoacaoDto.builder()
                    .idUsuarioBeneficiario(usuariosQuePodeReceberDoacao.get(1).getId())
                    .valorDoado(BigDecimal.valueOf(1000))
                    .build();
            operacaoService.criarDoacao(usuarioSetup.getUsuario1(), doacaoDto1);
            operacaoService.criarDoacao(usuarioSetup.getUsuario1(), doacaoDto2);
        }

        @Override
        protected String getQueryParams() {
            return "semDoacoes=sim";
        }

        @Test
        void deveRetornar3Usuarios() {
            assertThat(usuarios.size()).isEqualTo(3);
        }

        @Test
        void deveRetornarUsuariosComCampoPodeReceberDoacoesIgualAtrue() {
            assertThat(usuarios.stream().allMatch(UsuarioDto::getPodeReceberDoacoes)).isTrue();
        }

        @Test
        void deveRetornarApenasUsuariosQueReceberamMinhasDoacoes() {
            usuarios.forEach(usuarioDto -> assertThat(operacaoService.findAllByContaDestinoHash(usuarioDto.getContaDto().getHash()).size()).isEqualTo(0));
        }
    }

}
