package br.com.api.service;

import br.com.api.dto.ExtratoDto;
import br.com.api.dto.SaldoDto;
import br.com.api.exception.NotFoundException;
import br.com.api.model.Conta;
import br.com.api.model.Usuario;
import br.com.api.repository.ContaRepository;
import br.com.api.spec.ContaSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final OperacaoService operacaoService;


    @Autowired
    public ContaService(ContaRepository contaRepository,
                        OperacaoService operacaoService) {

        this.contaRepository = contaRepository;
        this.operacaoService = operacaoService;
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

    public Conta create(ContaSpec contaSpec, Usuario usuario) {
        Conta conta = Conta.criar(contaSpec, usuario);
        return contaRepository.save(conta);
    }

    public Conta atualizarConta(Conta conta) {
        return contaRepository.save(conta);
    }

    public List<ExtratoDto> listaOperacoesByHash(String contaHash) {
        return operacaoService.getExtrato(contaHash);
    }

    public List<Conta> listContasUsuario(Usuario usuario) {
        return contaRepository.getByUsuarioId(usuario.getId());
    }

    public SaldoDto listSaldo(String hash) {
        Conta conta = findByHash(hash);
        return SaldoDto.builder()
                .hash(conta.getHash())
                .saldo(getSaldo(hash))
                .build();
    }
}