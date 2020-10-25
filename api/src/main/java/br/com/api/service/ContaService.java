package br.com.api.service;

import br.com.api.exception.ApplicationException;
import br.com.api.exception.NotFoundException;
import br.com.api.model.Conta;
import br.com.api.model.Usuario;
import br.com.api.repository.ContaRepository;
import br.com.api.spec.ContaSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    @Autowired
    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public List<Conta> listAll() {
        return contaRepository.findAll();
    }

    public Conta findByHash(String contaHash, Usuario usuario) {
        if (!usuario.eAdmin() && !contaHash.equals(usuario.getContaHash())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Só pode buscar sua conta");
        }
        return contaRepository.findByHash(contaHash).orElseThrow(() -> new NotFoundException("Conta com hash " + contaHash));
    }

    public Conta findByHash(String contaHash) {
        return contaRepository.findByHash(contaHash).orElseThrow(() -> new NotFoundException("Conta com hash " + contaHash));
    }

    public Conta findById(Long id, Usuario usuario) {

        Conta contaUsuario = findByHash(usuario.getContaHash());

        if (!usuario.eAdmin() && id.compareTo(contaUsuario.getId()) !=0) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Só pode buscar sua conta");
        }
        return contaRepository.findById(id).orElseThrow(() -> new NotFoundException("Conta com id " + id));
    }

    //TODO: corrigir depois

    public Conta create(ContaSpec contaSpec, Usuario usuario) {
        Conta conta = Conta.criar(contaSpec, usuario);
        conta.setUsuario(null);
        conta = contaRepository.save(conta);
        conta.setUsuario(usuario);
        return contaRepository.save(conta);
    }

    public Conta atualizarConta(Conta conta) {
        return contaRepository.save(conta);
    }

    public List<Conta> listContasUsuario(Usuario usuario) {
        return contaRepository.getByUsuarioId(usuario.getId());
    }

    public Double listSaldo(String hash, Usuario usuario) {
        if (usuario.eAdmin()) {
            return findByHash(hash, usuario).getSaldo();
        }
        if (usuario.getContaHash().equals(hash)) {
            return findByHash(hash, usuario).getSaldo();
        }
        throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Não é permitido ver saldo de outra conta");
    }
}