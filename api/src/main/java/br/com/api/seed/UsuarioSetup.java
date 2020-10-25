package br.com.api.seed;

import br.com.api.dto.OperacaoDto;
import br.com.api.model.Usuario;
import br.com.api.repository.ContaRepository;
import br.com.api.repository.UsuarioRepository;
import br.com.api.service.OperacaoService;
import br.com.api.service.UsuarioService;
import br.com.api.spec.UsuarioSpec;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UsuarioSetup {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OperacaoService operacaoService;
    private final ContaRepository contaRepository;

    @Getter
    private Usuario usuario1;

    @Getter
    private Usuario usuario2;

    @Getter
    private Usuario admin;

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
        operacaoService.deposito(usuario1.getContaHash(), new OperacaoDto("DEPOSITO", BigDecimal.valueOf(1000D)));
        operacaoService.deposito(usuario2.getContaHash(), new OperacaoDto("DEPOSITO", BigDecimal.valueOf(1000D)));

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
}