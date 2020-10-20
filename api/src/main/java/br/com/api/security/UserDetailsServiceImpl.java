package br.com.api.security;

import br.com.api.model.Usuario;
import br.com.api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;


    @Autowired
    public UserDetailsServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByEmail(username);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(usuario.get_id())
                .email(usuario.getEmail())
                .name(usuario.getNome())
                .authorities(List.of(new SimpleGrantedAuthority(usuario.getPerfil().name().toUpperCase())))
                .password(usuario.getSenha())
                .build();

        return userDetails;
    }

    public Usuario byEmail(String email) {
        return usuarioService.findByEmail(email);
    }

}
