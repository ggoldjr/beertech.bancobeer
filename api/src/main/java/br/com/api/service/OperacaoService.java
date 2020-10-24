package br.com.api.service;

import br.com.api.dto.ExtratoDto;
import br.com.api.dto.OperacaoDto;
import br.com.api.dto.TransferenciaDto;
import br.com.api.exception.SaldoInsuficienteException;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.model.Usuario;
import br.com.api.repository.OperacaoRepository;
import br.com.api.security.UsuarioLogado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Operacao deposito(String contaHash, OperacaoDto operacaoDto) {
        Conta conta = contaService.findByHash(contaHash);
        Operacao operacao = Operacao.criar(operacaoDto, conta);
        conta.deposito(operacao.getValor());
        return operacaoRepository.save(operacao);
    }

    public List<Operacao> getOperacaoDaConta(String contaHash) {
        return  operacaoRepository.findAllByContaId(contaHash);
    }

    public List<ExtratoDto> getExtrato(String contaHash, Usuario usuario) {
        if (!usuario.eAdmin() && !contaHash.equals(usuario.getContaHash())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Não pode ver extrato de outras contas");
        }
        return operacaoRepository.findAllByHashContaDestinoOrConta_Hash(contaHash).stream()
                .map(operacao -> {
                    ExtratoDto.ExtratoDtoBuilder extratoDtoBuilder = ExtratoDto.builder()
                            .valor(operacao.getValor())
                            .data(operacao.getDataOperacao())
                            .hashContaOrigem(operacao.getHashContaDestino());
                    if (operacao.getHashContaDestino().equals(usuario.getContaHash())) {
                        extratoDtoBuilder.tipo(Operacao.Tipo.TRANSFERENCIA_RECEBIDA.name());
                    } else {
                        extratoDtoBuilder.tipo(operacao.getTipo().name());
                    }
                    return extratoDtoBuilder.build();
                }).collect(Collectors.toList());
    }

    public Operacao criarTransferencia(TransferenciaDto transferenciaDto, Usuario usuario) {
        Conta contaOrigem =  contaService.findByHash(transferenciaDto.getHashContaOrigem(), usuario);
        if (contaOrigem.getUsuario().getId() != usuario.getId()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Só pode transferir dinheiro da sua conta");
        }
        Double valor = transferenciaDto.getValor();
        if (!contaOrigem.saldoEmaiorOrIgualA(valor)) throw new SaldoInsuficienteException();
        Conta contaDestino = contaService.findByHash(transferenciaDto.getHashContaDestino(), usuario);
        Operacao operacao = Operacao.builder()
                .conta(contaOrigem)
                .hashContaDestino(contaDestino.getHash())
                .valor(valor)
                .tipo(Operacao.Tipo.TRANSFERENCIA)
                .build();
        operacao = operacaoRepository.save(operacao);
        contaDestino.deposito(contaOrigem.saque(valor, contaOrigem.getUsuario()));
        contaService.atualizarConta(contaOrigem);
        contaService.atualizarConta(contaDestino);
        return operacao;
    }
}
