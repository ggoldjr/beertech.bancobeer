package br.com.api.repository;

import br.com.api.model.Conta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  ContaRepository  extends MongoRepository<Conta, String> {

    Optional<Conta> findByHash(String contaHash);
}
