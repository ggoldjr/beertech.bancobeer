package br.com.api.repository;

import br.com.api.model.Operacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperacaoRepository extends MongoRepository<Operacao, String> {
//    Optional<Operacao> findByContaAnd_id(String id);

    List<Operacao> findAllByContaHash(String contaHash);
}

