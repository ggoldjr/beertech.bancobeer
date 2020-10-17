package br.com.api.security;

import br.com.api.exception.SenhaInvalidaException;
import br.com.api.model.Usuario;
import br.com.api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
                .email(usuario.getEmail())
                .name(usuario.getNome())
                .authorities(new ArrayList<GrantedAuthority>())
                .password(usuario.getSenha())
                .build();

        return userDetails;
    }

    public UserDetails autenticar( Usuario usuario ) throws SenhaInvalidaException{
        UserDetails user = loadUserByUsername(usuario.getEmail());
        boolean senhasBatem = false;//encoder.matches( usuario.getSenha(), user.getPassword() );

        if(senhasBatem){
            return user;
        }

        throw new SenhaInvalidaException();
    }

}
