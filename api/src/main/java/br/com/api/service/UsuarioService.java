package br.com.api.service;

import br.com.api.dto.AlterarSenhaDto;
import br.com.api.dto.HabilitarOrDesabilitarDoacaoDto;
import br.com.api.exception.ApplicationException;
import br.com.api.exception.NotFoundException;
import br.com.api.model.Conta;
import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import br.com.api.spec.AtualizarUsuarioSpec;
import br.com.api.spec.ContaSpec;
import br.com.api.spec.UsuarioSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContaService contaService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OperacaoService operacaoService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          ContaService contaService,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          OperacaoService operacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.contaService = contaService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.operacaoService = operacaoService;
    }

    //todo: refatorar depois
    public Usuario criar(UsuarioSpec usuarioSpec){
        Usuario usuario = Usuario.criar(usuarioSpec);
        usuario.setSenha(bCryptPasswordEncoder.encode(usuario.getSenha()));
        usuario = usuarioRepository.save(usuario);
        ContaSpec contaSpec = ContaSpec.builder().idUsuario(usuario.getId()).build();
        Conta conta = contaService.create(contaSpec, usuario);
        usuario.setContaHash(conta.getHash());
        usuario = usuarioRepository.save(usuario);
        return resolveConta(usuario);
    }

    public Usuario buscarPorEmail(String email, Usuario usuario){
        if (!usuario.eAdmin() && !email.equals(usuario.getEmail())) {
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
        List<Usuario> collect = usuarioRepository.findAllByIdIsNot(usuario.getId()).stream()
                .map(this::resolveConta)
                .collect(Collectors.toList());
        return collect;
    }


    public Usuario update(AtualizarUsuarioSpec usuarioSpec, Usuario usuarioLogado) {

        Usuario usuarioValidacao = usuarioRepository.findByEmail(usuarioSpec.getEmail()).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));

        if (usuarioValidacao.getId().compareTo(usuarioLogado.getId())==0) {
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
        if (usuarioLogado.getId().compareTo(alterarSenhaDto.getIdUsuario())!=0) {
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

    //todo: refatorar

    public List<Usuario> listaUsuarios(Usuario usuario, String podeReceberDoacao, String minhasDoacoes, String semDoacoes) {

        List<Usuario> all = usuarioRepository.findAllByIdIsNot(usuario.getId());

        if (!podeReceberDoacao.isEmpty() && podeReceberDoacao.equals("sim")) {
            all = all.stream().filter(operacaoService::podeReceber).collect(Collectors.toList());
        }

        if (!minhasDoacoes.isEmpty() && minhasDoacoes.equals("sim")) {
            List<Long> ids = operacaoService.findAllByAndHashUsuarioDoador(usuario.getContaHash()).stream()
                    .map(operacao -> operacao.getConta().getUsuario().getId())
                    .collect(Collectors.toList());
            all = all.stream().filter(u -> ids.contains(u.getId())).collect(Collectors.toList());
        }

        if (!semDoacoes.isEmpty() && semDoacoes.equals("sim")) {
            List<Long> ids = operacaoService.doacoesDoMes().stream()
                    .map(doacao -> doacao.getConta().getUsuario().getId())
                    .collect(Collectors.toList());
            all = all.stream().filter(usuario1 -> !ids.contains(usuario1.getId())).collect(Collectors.toList());
        }

        return all.stream()
                .filter(usuario1 -> usuario1.getPerfil() != Usuario.Perfil.ADMIN)
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
        usuario.setContaDto(conta.toContaDto());
        return usuario;
    }
}
