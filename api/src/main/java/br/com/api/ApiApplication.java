package br.com.api;

import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@SpringBootApplication
@EnableJpaAuditing
public class ApiApplication implements CommandLineRunner {


	public final UsuarioRepository usuarioRepository ;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public ApiApplication(  UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Optional<Usuario> usuario = usuarioRepository.findByEmail("duplomalte@gmail.com");
		if(!usuario.isPresent()){
			usuarioRepository.save(Usuario.builder()
							.senha(bCryptPasswordEncoder.encode("senha"))
							.email("duplomalte@gmail.com")
							.cnpj("1234")
							.nome("Duplo Malte")
							.perfil(Usuario.Perfil.ADMIN)
							.podeReceberDoacoes(false)
							.build());
		}
	}
}