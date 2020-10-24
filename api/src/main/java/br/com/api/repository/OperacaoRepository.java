package br.com.api.repository;

import br.com.api.model.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperacaoRepository extends JpaRepository<Operacao, Long> {
    Optional<Operacao> findByContaId(Long id);

    List<Operacao> findAllByContaId(String contaHash);

    List<Operacao> findAllByhashContaDestino(String contaHash);

    List<Operacao> findAllByHashContaDestinoOrConta_Hash(String contaHash);

    void deleteByContaId(Long id);


}

