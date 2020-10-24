package br.com.api.service;

import br.com.api.dto.ExtratoDto;
import br.com.api.dto.OperacaoDto;
import br.com.api.dto.TransferenciaDto;
import br.com.api.exception.SaldoInsuficienteException;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.repository.OperacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OperacaoService {

    private final OperacaoRepository operacaoRepository;
    private final ContaService contaService;

    @Autowired
    public OperacaoService(OperacaoRepository operacaoRepository, ContaService contaService) {
        this.operacaoRepository = operacaoRepository;
        this.contaService = contaService;
    }

    public Operacao criar(String contaHash, OperacaoDto dto) {
        Conta conta = contaService.findByHash(contaHash);
        Operacao operacao = Operacao.criar(dto, conta);
        if(operacao.getTipo() == Operacao.Tipo.SAQUE) {
            conta.saque(operacao.getValor());
        } else {
            conta.deposito(operacao.getValor());
        }
        return operacaoRepository.save(operacao);
    }

    public List<Operacao> getOperacaoDaConta(String contaHash) {
        return  operacaoRepository.findAllByContaId(contaHash);
    }

    public List<Operacao> getOperacoesConta(String contaHash) {
        return  operacaoRepository.findAllByContaId(contaHash);
    }

    public List<Operacao> getTransferenciasInConta(String contaHash) {
        return  operacaoRepository.findAllByhashContaDestino(contaHash);
    }

    public List<ExtratoDto> getExtrato(String contaHash) {
        List<ExtratoDto> listaOperacao = getOperacoesConta(contaHash).stream()
                .map(e -> new ExtratoDto(e.getTipo().name(), e.getValor(), e.getCriado_em(), null, e.getHashContaDestino()))
                .collect(Collectors.toList());
        List<ExtratoDto> listaTransferencia = getTransferenciasInConta(contaHash)
                .stream()
                .map(e -> new ExtratoDto("TRANSFERENCIA RECEBIDA", e.getValor(), e.getCriado_em(), e.getConta().getHash(), null))
                .collect(Collectors.toList());
        listaOperacao.addAll(listaTransferencia);
        return listaOperacao;
    }

    public CompletableFuture<Operacao> criarTransferencia(TransferenciaDto transferenciaDto) {
        return CompletableFuture.supplyAsync(() -> {
            Conta contaOrigem =  contaService.findByHash(transferenciaDto.getHashContaOrigem());
            Conta contaDestino = contaService.findByHash(transferenciaDto.getHashContaDestino());
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
            contaService.atualizarConta(contaOrigem);
            contaService.atualizarConta(contaDestino);
            return operacao;
        });
    }
}
