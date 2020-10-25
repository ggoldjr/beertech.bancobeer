package br.com.api.seed;

import br.com.api.dto.OperacaoDto;
import br.com.api.model.Usuario;
import br.com.api.repository.ContaRepository;
import br.com.api.repository.UsuarioRepository;
import br.com.api.service.OperacaoService;
import br.com.api.service.UsuarioService;
import br.com.api.spec.UsuarioSpec;
import com.github.javafaker.Faker;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UsuarioSetup {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OperacaoService operacaoService;
    private final ContaRepository contaRepository;
    public Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    @Getter
    private Usuario usuario1;

    @Getter
    private Usuario usuario2;

    @Getter
    private Usuario admin;

    @Getter
    private List<Usuario> usuarios;

    @Autowired
    public UsuarioSetup(UsuarioService usuarioService,
                        UsuarioRepository usuarioRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder,
                        OperacaoService operacaoService, ContaRepository contaRepository) {

        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.operacaoService = operacaoService;
        this.contaRepository = contaRepository;
    }

    //TODO: corrigir para deletar conta ao deletar usuário

    public void setup() {
        deleteAll();
        UsuarioSpec usuarioParaCriar1 = UsuarioSpec.builder()
                .nome("Usuario teste 1")
                .email("teste1@gmail.com")
                .senha("senha")
                .cnpj("82826677000148")
                .build();

        UsuarioSpec usuarioParaCriar2 = UsuarioSpec.builder()
                .nome("Usuario teste 2")
                .email("teste2@gmail.com")
                .senha("senha")
                .cnpj("48028968000152")
                .build();
        usuario1 = usuarioService.criar(usuarioParaCriar1);
        usuario2 = usuarioService.criar(usuarioParaCriar2);
        operacaoService.deposito(usuario1.getContaHash(), new OperacaoDto("DEPOSITO", BigDecimal.valueOf(2000D)));
        operacaoService.deposito(usuario2.getContaHash(), new OperacaoDto("DEPOSITO", BigDecimal.valueOf(2000D)));

        admin = Usuario.builder()
                .nome("Duplo Malte")
                .email("duplomalte@gmail.com")
                .senha(bCryptPasswordEncoder.encode("senha"))
                .cnpj("49415525000187")
                .perfil(Usuario.Perfil.ADMIN)
                .podeReceberDoacoes(false)
                .build();
        usuarioRepository.save(admin);
    }

    public void deleteAll() {
        contaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    public void criarUsuario(int quantidade) {
        usuarios = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            boolean podeReceberDoacoes = false;
            if (quantidade / 2 <= i) {
                podeReceberDoacoes = true;
            }
            UsuarioSpec usuarioSpec = UsuarioSpec.builder()
                    .nome(faker.name().fullName())
                    .email(faker.name().firstName() + i + "@gmail.com")
                    .senha("senha")
                    .cnpj(CpfAndCnpjGenerator.cnpj())
                    .build();
            Usuario usuario = usuarioService.criar(usuarioSpec);
            usuario.setPodeReceberDoacoes(podeReceberDoacoes);
            Usuario usuarioAtualizado = usuarioRepository.save(usuario);
            usuarios.add(usuarioAtualizado);
        }
    }

    public List<Usuario> getUsuariosQuePodeReceberDoacao() {
        return usuarios.stream().filter(Usuario::getPodeReceberDoacoes).collect(Collectors.toList());
    }
}