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
        contaRepository.deleteAll();
        usuarioRepository.deleteAll();
        UsuarioSpec usuarioParaCriar1 = UsuarioSpec.builder()
                .nome("Usuario teste 1")
                .email("teste1@gmail.com")
                .senha(bCryptPasswordEncoder.encode("senha"))
                .cnpj("82826677000148")
                .perfil(Usuario.Perfil.USUARIO)
                .build();

        UsuarioSpec usuarioParaCriar2 = UsuarioSpec.builder()
                .nome("Usuario teste 2")
                .email("teste2@gmail.com")
                .senha(bCryptPasswordEncoder.encode("senha"))
                .cnpj("48028968000152")
                .perfil(Usuario.Perfil.USUARIO)
                .build();
        usuario1 = usuarioService.criar(usuarioParaCriar1);
        usuario2 = usuarioService.criar(usuarioParaCriar2);
        operacaoService.deposito(usuario1.getContaHash(), new OperacaoDto("DEPOSITO", 1000D));
        operacaoService.deposito(usuario2.getContaHash(), new OperacaoDto("DEPOSITO", 1000D));

        admin = Usuario.builder()
                .nome("Admin admin")
                .email("admin@gmail.com")
                .senha(bCryptPasswordEncoder.encode("senha"))
                .cnpj("49415525000187")
                .perfil(Usuario.Perfil.ADMIN)
                .build();
        usuarioRepository.save(admin);
    }
}