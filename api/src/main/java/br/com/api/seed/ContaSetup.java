package br.com.api.seed;

import br.com.api.model.Conta;
import br.com.api.repository.ContaRepository;
import br.com.api.repository.OperacaoRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ContaSetup {

    private final OperacaoRepository operacaoRepository;
    private final ContaRepository contaRepository;
    @Getter
    private List<Conta> contas = new ArrayList<>();

    @Autowired
    public ContaSetup(OperacaoRepository operacaoRepository, ContaRepository contaRepository) {
        this.operacaoRepository = operacaoRepository;
        this.contaRepository = contaRepository;
    }

    public void setup() {
        operacaoRepository.deleteAll();
        contaRepository.deleteAll();
        contas = Stream.iterate(1, i -> i + 1)
                .map(integer -> criar())
                .limit(10)
                .collect(Collectors.toList());
    }

    public Conta criar() {
        Conta conta = Conta.builder()
                ._id(UUID.randomUUID().toString())
                .saldo(1000d)
                .hash(UUID.randomUUID().toString())
                .build();
        return contaRepository.save(conta);
    }
}
