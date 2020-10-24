package br.com.api.service;

import br.com.api.dto.*;
import br.com.api.exception.NotFoundException;
import br.com.api.model.Conta;
import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import br.com.api.spec.ContaSpec;
import br.com.api.spec.UsuarioSpec;
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

    public Usuario create(UsuarioSpec usuarioSpec){
        Usuario usuario = Usuario.criar(usuarioSpec);
        usuario = usuarioRepository.save(usuario);
        ContaSpec contaSpec = ContaSpec.builder().idUsuario(usuario.getId()).build();
        Conta conta = contaService.create(contaSpec, usuario);
        usuario.setContaHash(conta.getHash());
        return usuarioRepository.save(usuario);
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


    public Usuario update(Usuario usuarioParaAtualizar) {
        findById(usuarioParaAtualizar.getId());
        return usuarioRepository.save(usuarioParaAtualizar);
    }


    public void updatePassword(AlterarSenhaDto request) {
        Usuario usuario = findById(request.getIdUsuario());
        if(request.getSenhaAntiga().matches(request.getSenhaNova())){
            throw new RuntimeException("Senha nova não pode ser igual a antiga");
        }
        if (!bCryptPasswordEncoder.matches(request.getSenhaAntiga(),usuario.getSenha())){
            throw new RuntimeException("Senha antiga inválida");
        }
        usuario.setSenha(bCryptPasswordEncoder.encode(request.getSenhaNova()));
        usuarioRepository.save(usuario);
    }

    public List<Usuario> usuariosQuePodemReceberDoacao(Long id) {
        return usuarioRepository.findAllByPodeReceberDoacoesTrue().stream().filter(usuario -> usuario.getId() != id)
                .collect(Collectors.toList());
    }

    public void habilitarOuDesabilitarDoacao(HabilitarOrDesabilitarDoacaoDto habilitarOrDesabilitarDoacaoDto) {
        Usuario usuario = findById(habilitarOrDesabilitarDoacaoDto.getIdUsuario());
        usuario.setPodeReceberDoacoes(habilitarOrDesabilitarDoacaoDto.getPodeReceberDoacao());
        usuarioRepository.save(usuario);
    }
}
