package br.com.api.security;

import br.com.api.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioLogado implements UserDetails {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String hashConta;
    private Collection<? extends GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Usuario toUsuario() {
        Usuario.Perfil perfil = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(s -> Usuario.Perfil.valueOf(s.toUpperCase()))
                .findFirst().get();

        return Usuario.builder()
                .nome(this.name)
                .email(this.email)
                .senha(this.password)
                .contaHash(this.hashConta)
                .perfil(perfil)
                .build();
    }
}
