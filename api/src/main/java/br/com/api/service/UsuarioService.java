package br.com.api.service;

import br.com.api.dto.AlterarSenhaDto;
import br.com.api.dto.UsuarioDto;
import br.com.api.dto.UsuarioDtoIn;
import br.com.api.exception.NotFoundException;
import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContaService contaService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, ContaService contaService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.contaService = contaService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Usuario save(Usuario usuario){
        try {
            usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

            return usuarioRepository.save(usuario);
        }
        catch (Throwable e){
            throw e;
        }

    }

    public Usuario findByEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
    }

    public Usuario findById(Long id){
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
    }

    public List<Usuario> listAll(){
        return usuarioRepository.findAll();
    }


    public UsuarioDto update(Usuario usuarioRequest) {

        findById(usuarioRequest.getId());
        return usuarioToUsuarioDto(save(usuarioRequest));

    }

    public UsuarioDto create(UsuarioDtoIn req){

        return usuarioToUsuarioDto(save(usuarioDtoInToUsuario(req)));


    }

    public List<UsuarioDto> list(){
        return listAll().stream()
                .map( usuario -> usuarioToUsuarioDto(usuario))
                .collect(Collectors.toList());
    }

    public UsuarioDto listByEmail(String email){

        return usuarioToUsuarioDto(findByEmail(email));

    }

    public UsuarioDto usuarioToUsuarioDto(Usuario usuario){

        return UsuarioDto.builder()
                .id(usuario.getId())
                .cnpj(usuario.getCnpj())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .perfil(usuario.getPerfil())
                .contas(contaService.listContasUsuario(usuario))
                .build();

    }

    public Usuario usuarioDtoInToUsuario (UsuarioDtoIn req){

        return Usuario.builder()
                .perfil(req.getPerfil())
                .cnpj(req.getCnpj())
                .email(req.getEmail())
                .nome(req.getNome())
                .senha(req.getSenha())
                .build();

    }

    public void updatePassword(AlterarSenhaDto request) throws Exception {

        Usuario usuario = findById(request.getIdUsuario());

        if(request.getSenhaAntiga().matches(request.getSenhaNova())){
            throw new Exception("Senha nova não pode ser igual a antiga");
        }

        if (!bCryptPasswordEncoder.matches(request.getSenhaAntiga(),usuario.getSenha())){
            throw new Exception("Senha antiga inválida");
        }

        usuario.setSenha(bCryptPasswordEncoder.encode(request.getSenhaNova()));

        usuarioRepository.save(usuario);

    }

    public List<Usuario> usuariosQuePodemReceberDoacao(Long id) {
        return usuarioRepository.findAllByPodeReceberDoacoesTrue().stream().filter(usuario -> usuario.getId() != id)
                .collect(Collectors.toList());
    }
}
