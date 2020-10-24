package br.com.api.seed;

import br.com.api.dto.OperacaoDto;
import br.com.api.dto.UsuarioDto;
import br.com.api.dto.UsuarioDtoIn;
import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import br.com.api.service.ContaService;
import br.com.api.service.UsuarioService;
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
    @Getter
    private Usuario usuario1;
    @Getter
    private Usuario usuario2;

    @Autowired
    public UsuarioSetup(UsuarioService usuarioService, UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptPasswordEncoder, ContaService contaService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.contaService = contaService;
    }

    public void setup() {
        usuarioRepository.deleteAll();
        UsuarioDtoIn usuarioParaCriar1 = UsuarioDtoIn.builder()
                .nome("Usuario teste 1")
                .email("teste1@gmail.com")
                .senha(bCryptPasswordEncoder.encode("senha"))
                .cnpj("82826677000148")
                .perfil(Usuario.Perfil.USUARIO)
                .build();

        UsuarioDtoIn usuarioParaCriar2 = UsuarioDtoIn.builder()
                .nome("Usuario teste 2")
                .email("teste2@gmail.com")
                .senha(bCryptPasswordEncoder.encode("senha"))
                .cnpj("48028968000152")
                .perfil(Usuario.Perfil.USUARIO)
                .build();
        UsuarioDto usuarioDto1 = usuarioService.create(usuarioParaCriar1);
        UsuarioDto usuarioDto2 = usuarioService.create(usuarioParaCriar2);

        usuario1 = usuarioService.findById(usuarioDto1.getId());
        usuario2 = usuarioService.findById(usuarioDto2.getId());

        contaService.criarOperacao(new OperacaoDto("DEPOSITO", 1000D), usuario1.getContaHash());
        contaService.criarOperacao(new OperacaoDto("DEPOSITO", 1000D), usuario2.getContaHash());
    }
}
