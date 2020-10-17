package br.com.api;

import br.com.api.model.Usuario;
import br.com.api.repository.UsuarioRepository;
import br.com.api.seed.ContaSetup;
import br.com.api.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
public class ApiApplication implements CommandLineRunner {


	public final ContaSetup contaSetup;
	public final UsuarioService usarioService;
	public final UsuarioRepository usuarioRepository ;

	@Autowired
	public ApiApplication(ContaSetup contaSetup, UsuarioService usarioService, UsuarioRepository usuarioRepository) {
		this.contaSetup = contaSetup;
		this.usarioService = usarioService;
		this.usuarioRepository = usuarioRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Override
	public void run(String... args) {
		//contaSetup.setup();

		usuarioRepository.deleteAll();

		Usuario usuario = Usuario.builder()
				.senha(new BCryptPasswordEncoder().encode("senha"))
				.email("duplomalte@gmail.com")
				.cnpj("1234")
				.nome("Duplo Malte")
				.perfil(Usuario.Perfil.ADMIN)
				.build();

		usarioService.save(usuario);

	}

}
