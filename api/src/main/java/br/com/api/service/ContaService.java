package br.com.api.service;

import br.com.api.dto.OperacaoDto;
import br.com.api.exception.NotFoundException;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final OperacaoService operacaoService;

    @Autowired
    public ContaService(ContaRepository contaRepository, OperacaoService operacaoService) {
        this.contaRepository = contaRepository;
        this.operacaoService = operacaoService;
    }

    public List<Conta> listAll() {
        return contaRepository.findAll();
    }

    public Conta findByHash(String contaHash) {
        return contaRepository.findByHash(contaHash).orElseThrow(() -> new NotFoundException("Conta com hash " + contaHash));
    }
    public Double getSaldo(String contaHash) {
        return findByHash(contaHash).getSaldo();
    }

    public CompletableFuture<Operacao> criarOperacao(OperacaoDto operacaoDto, String hashConta) {
        return CompletableFuture.supplyAsync(() -> {
            Conta conta = findByHash(hashConta);
            Operacao operacao = operacaoService.criar(conta, operacaoDto);
            conta.setSaldo(operacao.getConta().getSaldo());
            contaRepository.save(conta);
            return operacao;
        });
    }

    public Conta criarConta() {
        Conta conta = Conta.builder()
                .hash(UUID.randomUUID().toString())
                .saldo(0d)
                .build();
        return contaRepository.save(conta);
    }

    public Conta atualizarConta(Conta conta) {
        return contaRepository.save(conta);
    }
}
