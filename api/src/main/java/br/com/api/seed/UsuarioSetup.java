package br.com.api.seed;

import br.com.api.dto.OperacaoDto;
import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import br.com.api.service.ContaService;
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
    private final ContaService contaService;
    private final OperacaoService operacaoService;
    @Getter
    private Usuario usuario1;
    @Getter
    private Usuario usuario2;

    @Autowired
    public UsuarioSetup(UsuarioService usuarioService,
                        UsuarioRepository usuarioRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder,
                        ContaService contaService,
                        OperacaoService operacaoService) {

        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.contaService = contaService;
        this.operacaoService = operacaoService;
    }

    public void setup() {
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
        operacaoService.criar(usuario1.getContaHash(), new OperacaoDto("DEPOSITO", 1000D));
        operacaoService.criar(usuario2.getContaHash(), new OperacaoDto("DEPOSITO", 1000D));
    }
}