package br.com.api;

import br.com.api.seed.ContaSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication implements CommandLineRunner {


	public final ContaSetup contaSetup;

	@Autowired
	public ApiApplication(ContaSetup contaSetup) {
		this.contaSetup = contaSetup;
	}

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Override
	public void run(String... args) {
		contaSetup.criar();
	}

}
