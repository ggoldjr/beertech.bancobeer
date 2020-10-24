package br.com.api.service;

import br.com.api.dto.AlterarSenhaDto;
import br.com.api.dto.HabilitarOrDesabilitarDoacaoDto;
import br.com.api.exception.NotFoundException;
import br.com.api.model.Conta;
import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import br.com.api.security.UsuarioLogado;
import br.com.api.spec.AtualizarUsuarioSpec;
import br.com.api.spec.ContaSpec;
import br.com.api.spec.UsuarioSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    //todo: refatorar depois
    public Usuario criar(UsuarioSpec usuarioSpec){
        Usuario usuario = Usuario.criar(usuarioSpec);
        usuario = usuarioRepository.save(usuario);
        ContaSpec contaSpec = ContaSpec.builder().idUsuario(usuario.getId()).build();
        Conta conta = contaService.create(contaSpec, usuario);
        usuario.setContaHash(conta.getHash());
        usuario = usuarioRepository.save(usuario);
        return resolveConta(usuario);
    }

    public Usuario buscarPorEmail(String email, Usuario usuario){
        if (!usuario.eAdmin() && email.equals(usuario.getEmail())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Só pode buscar sua usuário");
        }
        Usuario usuarioSalvo = usuarioRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
        return resolveConta(usuarioSalvo);
    }

    public Usuario buscarPorId(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
        return resolveConta(usuario);
    }

    public List<Usuario> listAll(Usuario usuario){
        return usuarioRepository.findAllByIdIsNot(usuario.getId()).stream()
                .map(this::resolveConta)
                .collect(Collectors.toList());
    }

    public Usuario update(AtualizarUsuarioSpec usuarioSpec, Usuario usuarioLogado) {
        if (usuarioLogado.getEmail().equals(usuarioSpec.getEmail())) {
            Usuario usuarioParaAtualizar = buscarPorId(usuarioLogado.getId());
            usuarioParaAtualizar.setEmail(usuarioSpec.getEmail());
            usuarioParaAtualizar.setCnpj(usuarioSpec.getCnpj());
            usuarioParaAtualizar.setNome(usuarioSpec.getNome());
            Usuario usuario = usuarioRepository.save(usuarioParaAtualizar);
            return resolveConta(usuario);
        }
        throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Não pode atualizar informações dos outros usuários");
    }

    public void updatePassword(AlterarSenhaDto alterarSenhaDto, Usuario usuarioLogado) {
        if (usuarioLogado.getId() != alterarSenhaDto.getIdUsuario()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Não pode atualizar senha dos outros usuários");
        }
        Usuario usuario = buscarPorId(alterarSenhaDto.getIdUsuario());
        if(alterarSenhaDto.getSenhaAntiga().matches(alterarSenhaDto.getSenhaNova())){
            throw new RuntimeException("Senha nova não pode ser igual a antiga");
        }
        if (!bCryptPasswordEncoder.matches(alterarSenhaDto.getSenhaAntiga(),usuario.getSenha())){
            throw new RuntimeException("Senha antiga inválida");
        }
        usuario.setSenha(bCryptPasswordEncoder.encode(alterarSenhaDto.getSenhaNova()));
        usuarioRepository.save(usuario);
    }

    public List<Usuario> usuariosQuePodemReceberDoacao(Long id) {
        return usuarioRepository.findAllByPodeReceberDoacoesTrue().stream()
                .filter(usuario -> usuario.getId() != id)
                .map(this::resolveConta)
                .collect(Collectors.toList());
    }

    public void habilitarOuDesabilitarDoacao(HabilitarOrDesabilitarDoacaoDto habilitarOrDesabilitarDoacaoDto) {
        Usuario usuario = buscarPorId(habilitarOrDesabilitarDoacaoDto.getIdUsuario());
        usuario.setPodeReceberDoacoes(habilitarOrDesabilitarDoacaoDto.getPodeReceberDoacao());
        usuarioRepository.save(usuario);
    }

    private Usuario resolveConta(Usuario usuario) {
        Conta conta = contaService.findByHash(usuario.getContaHash());
        usuario.setConta(conta);
        return usuario;
    }
}
