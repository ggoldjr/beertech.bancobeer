package br.com.api.repository;

import br.com.api.model.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperacaoRepository extends JpaRepository<Operacao, Long> {

    Optional<Operacao> findByContaId(Long id);

    List<Operacao> findAllByConta_Hash(String contaHash);

    List<Operacao> findAllByhashContaDestino(String contaHash);

    @Query("select o from Operacao o where o.hashContaDestino = :contaHash or o.conta.hash = :contaHash")
    List<Operacao> findByHashContaDestinoOrConta_Hash(String contaHash);

    @Query("select o from Operacao o where o.hashContaDestino = :hashContaDestino or o.tipo = :tipo")
    List<Operacao> findByHashContaDestinoAndTipo(String hashContaDestino, Operacao.Tipo tipo);

    @Query("select o from Operacao o where o.hashContaDestino = :hashContaOrigem or o.tipo = :tipo")
    List<Operacao> findByConta_HashAndTipo(String hashContaOrigem, Operacao.Tipo tipo);

    List<Operacao> findAllByDataOperacaoIsBetween(LocalDate start, LocalDate end);

}

