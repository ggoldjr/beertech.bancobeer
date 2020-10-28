package br.com.api;

import br.com.api.model.Usuario;
import br.com.api.repository.ContaRepository;
import br.com.api.repository.OperacaoRepository;
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
	public final ContaRepository contaRepository ;
	public final OperacaoRepository operacaoRepository ;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public ApiApplication(UsuarioRepository usuarioRepository, ContaRepository contaRepository, OperacaoRepository operacaoRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.contaRepository = contaRepository;
		this.operacaoRepository = operacaoRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Override
	public void run(String... args) {
		operacaoRepository.deleteAll();
		contaRepository.deleteAll();
		usuarioRepository.deleteAll();
		Optional<Usuario> usuario = usuarioRepository.findByEmail("duplomalte@gmail.com");
		if(usuario.isEmpty()){
			usuarioRepository.save(Usuario.builder()
							.senha(bCryptPasswordEncoder.encode("senha"))
							.email("duplomalte@gmail.com")
							.nome("Duplo Malte")
							.cnpj("47958997000150")
							.perfil(Usuario.Perfil.ADMIN)
							.podeReceberDoacoes(false)
							.build());
		}
	}



}