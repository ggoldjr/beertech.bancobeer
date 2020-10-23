package br.com.api.repository;

import br.com.api.model.Conta;
import br.com.api.model.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    List<Doacao> findAllByUsuarioId(Long usuarioBeneficiario);

}
