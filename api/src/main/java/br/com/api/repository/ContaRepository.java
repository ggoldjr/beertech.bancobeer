package br.com.api.repository;

import br.com.api.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  ContaRepository  extends JpaRepository<Conta, Long> {

    Optional<Conta> findByHash(String contaHash);

    List<Conta> getByUsuarioId(Long id);

}
