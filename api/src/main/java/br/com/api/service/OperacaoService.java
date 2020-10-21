package br.com.api.service;

import br.com.api.dto.OperacaoDto;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.repository.OperacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperacaoService {

    private final OperacaoRepository operacaoRepository;

    @Autowired
    public OperacaoService(OperacaoRepository operacaoRepository) {
        this.operacaoRepository = operacaoRepository;
    }

    public Operacao criar(Conta conta, OperacaoDto dto) {
        Operacao operacao = Operacao.criar(dto, conta);
        if(operacao.getTipo() == Operacao.Tipo.SAQUE) {
            conta.saque(operacao.getValor());
        } else {
            conta.deposito(operacao.getValor());
        }
        return operacaoRepository.save(operacao);
    }

    public List<Operacao> getOperacaoDaConta(String contaHash) {

        return  operacaoRepository.findAllByContaHash(contaHash);

    }

    public List<Operacao> getOperacoesConta(String contaHash) {

        return  operacaoRepository.findAllByContaHash(contaHash);

    }

    public List<Operacao> getTransferenciasInConta(String contaHash) {

        return  operacaoRepository.findAllByhashContaDestino(contaHash);

    }

    public List<OperacaoDto> getExtrato(String contaHash) {

        List<OperacaoDto> listaOperacao =
                getOperacoesConta(contaHash)
                .stream()
                        .map(e -> new OperacaoDto(e.getTipo().name(),e.getValor()) )
                        .collect(Collectors.toList());

        List<OperacaoDto> listaTransferencia =
                getTransferenciasInConta(contaHash)
                        .stream()
                        .map(e -> new OperacaoDto("TRANSFERENCIA RECEBIDA",e.getValor()) )
                        .collect(Collectors.toList());
        listaOperacao.addAll(listaTransferencia);

        return listaOperacao;


    }





}
