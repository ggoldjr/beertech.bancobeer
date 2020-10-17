package br.com.api.service;

import br.com.api.dto.OperacaoDto;
import br.com.api.dto.TransferenciaDto;
import br.com.api.exception.NotFoundException;
import br.com.api.exception.SaldoInsuficienteException;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.model.Transferencia;
import br.com.api.repository.ContaRepository;
import br.com.api.repository.OperacaoRepository;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final OperacaoService operacaoService;
    private final OperacaoRepository operacaoRepository;


    @Autowired
    public ContaService(ContaRepository contaRepository, OperacaoService operacaoService, OperacaoRepository operacaoRepository) {
        this.contaRepository = contaRepository;
        this.operacaoService = operacaoService;
        this.operacaoRepository = operacaoRepository;
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

    public CompletableFuture<Operacao> criarOperacao(TransferenciaDto transferenciaDto) {
        return CompletableFuture.supplyAsync(() -> {

            Conta contaOrigem = findByHash(transferenciaDto.getHashContaOrigem());
            Conta contaDestino = findByHash(transferenciaDto.getHashContaDestino());

            Double valor = transferenciaDto.getValor();
            if (!contaOrigem.saldoEmaiorOrIgualA(valor)) throw new SaldoInsuficienteException();

            Operacao operacao = Operacao.builder()
                    .conta(contaOrigem)
                    .hashContaDestino(contaDestino.getHash())
                    .valor(valor)
                    .tipo(Operacao.Tipo.TRANSFERENCIA)
                    .build();

            operacao = operacaoRepository.save(operacao);

            contaDestino.deposito(contaOrigem.saque(valor));
            atualizarConta(contaOrigem);
            atualizarConta(contaDestino);

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
