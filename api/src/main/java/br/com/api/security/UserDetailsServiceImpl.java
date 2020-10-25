package br.com.api.security;

import br.com.api.exception.NotFoundException;
import br.com.api.model.Conta;
import br.com.api.model.Usuario;
import br.com.api.repository.ContaRepository;
import br.com.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;

    @Autowired
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository, ContaRepository contaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.contaRepository = contaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
        String hashConta = contaRepository.getByUsuarioId(usuario.getId()).map(Conta::getHash).orElse(null);
        return UsuarioLogado.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .name(usuario.getNome())
                .authorities(List.of(new SimpleGrantedAuthority(usuario.getPerfil().name().toUpperCase())))
                .password(usuario.getSenha())
                .hashConta(hashConta)
                .build();
    }
}