package br.com.api.service;

import br.com.api.dto.*;
import br.com.api.exception.NotFoundException;
import br.com.api.exception.SaldoInsuficienteException;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.model.Usuario;
import br.com.api.repository.ContaRepository;
import br.com.api.repository.OperacaoRepository;
import br.com.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final OperacaoService operacaoService;
    private final OperacaoRepository operacaoRepository;
    private final UsuarioRepository usuarioRepository;


    @Autowired
    public ContaService(ContaRepository contaRepository, OperacaoService operacaoService, OperacaoRepository operacaoRepository, UsuarioRepository usuarioRepository) {
        this.contaRepository = contaRepository;
        this.operacaoService = operacaoService;
        this.operacaoRepository = operacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Conta> listAll() {
        return contaRepository.findAll();
    }

    public Conta findByHash(String contaHash) {
        return contaRepository.findByHash(contaHash).orElseThrow(() -> new NotFoundException("Conta com hash " + contaHash));
    }

    public Conta findById(Long id) {
        return contaRepository.findById(id).orElseThrow(() -> new NotFoundException("Conta com id " + id));
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

    public ContaDtoOut create(ContaDtoIn contaDtoIn) {

        Conta conta = criarConta(contaDtoIn);

        return contaToDtoOut(conta);


    }

    public List<ContaDtoOut> list() {
        return listAll().stream().map(conta -> contaToDtoOut(conta))
                .collect(Collectors.toList());
    }

    public ContaDtoOut listByHash(String hash) {
        return contaToDtoOut(findByHash(hash));
    }

    public ContaDtoOut listById(Long id) {
        return contaToDtoOut(findById(id));
    }

    public Conta criarConta(ContaDtoIn contaDtoIn) {
        Usuario usuario = usuarioRepository.findById(contaDtoIn.getIdUsuario()).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        Conta conta = Conta.builder()
                .hash(UUID.randomUUID().toString())
                .saldo(0d)
                .usuario(usuario)
                .build();
        return contaRepository.save(conta);


    }

    public Conta atualizarConta(Conta conta) {
        return contaRepository.save(conta);
    }


    public List<ExtratoDto> listaOperacoesByHash(String contaHash) {
        return operacaoService.getExtrato(contaHash);
    }

    public String getNomeUsuarioConta(Conta conta) {
        try {
            Usuario usuario = conta.getUsuario();
            return usuario.getNome();
        } catch (Exception e) {
            return "";
        }

    }

    public List<ContaDtoOut> listContasUsuario(Usuario usuario) {

        return contaRepository.getByUsuarioId(usuario.getId()).stream()
                .map(conta -> contaToDtoOut(conta)
                ).collect(Collectors.toList());

    }

    public ContaDtoOut contaToDtoOut(Conta conta) {
        return
                ContaDtoOut.builder()
                        .hash(conta.getHash())
                        .id(conta.getId())
                        .saldo(conta.getSaldo())
                        .usuario(getNomeUsuarioConta(conta)).build();
    }

    public SaldoDto listSaldo(String hash){

    Conta conta = findByHash(hash);

    SaldoDto saldoDto = SaldoDto.builder()
            .hash(conta.getHash())
            .saldo(getSaldo(hash))
            .build();

        return saldoDto;
    }

}
