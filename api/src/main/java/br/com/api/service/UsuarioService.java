package br.com.api.service;

import br.com.api.exception.NotFoundException;
import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario save(Usuario usuario){
        try {
            return usuarioRepository.save(usuario);
        }
        catch (Throwable e){
            throw e;
        }

    }

    public Usuario findByEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
    }

    public Usuario findById(String id){
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
    }

    public List<Usuario> listAll(){
        return usuarioRepository.findAll();
    }


    public Usuario update(Usuario usuarioRequest) {

        findById(usuarioRequest.get_id());
        return save(usuarioRequest);

    }
}
