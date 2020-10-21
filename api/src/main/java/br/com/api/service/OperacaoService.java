package br.com.api.service;

import br.com.api.dto.ExtratoDto;
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

    public List<ExtratoDto> getExtrato(String contaHash) {

        List<ExtratoDto> listaOperacao =
                getOperacoesConta(contaHash)
                .stream()
                        .map(e -> new ExtratoDto(e.getTipo().name(),e.getValor(),e.getCriado_em()) )
                        .collect(Collectors.toList());

        List<ExtratoDto> listaTransferencia =
                getTransferenciasInConta(contaHash)
                        .stream()
                        .map(e -> new ExtratoDto("TRANSFERENCIA RECEBIDA",e.getValor(),e.getCriado_em()) )
                        .collect(Collectors.toList());
        listaOperacao.addAll(listaTransferencia);

        return listaOperacao;


    }





}
