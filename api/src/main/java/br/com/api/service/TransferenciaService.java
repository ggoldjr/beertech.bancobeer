package br.com.api.service;

import br.com.api.dto.TransferenciaDto;
import br.com.api.exception.SaldoInsuficienteException;
import br.com.api.model.Conta;
import br.com.api.model.Transferencia;
import br.com.api.repository.TransferenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final ContaService contaService;

    @Autowired
    public TransferenciaService(TransferenciaRepository transferenciaRepository, ContaService contaService) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaService = contaService;
    }

    public Transferencia criar(TransferenciaDto transferenciaDto) {
        Conta contaOrigem = contaService.findByHash(transferenciaDto.getHashContaOrigem());
        Double valor = transferenciaDto.getValor();
        if (!contaOrigem.saldoEmaiorOrIgualA(valor)) throw new SaldoInsuficienteException();
        Conta contaDestino = contaService.findByHash(transferenciaDto.getHashContaDestino());
        Transferencia transferenciaParaSalvar = Transferencia.builder()
                .contaOrigem(contaOrigem)
                .contaDestinoHash(contaDestino.getHash())
                .valor(valor)
                .build();
        Transferencia transferencia = transferenciaRepository.save(transferenciaParaSalvar);
        contaDestino.deposito(contaOrigem.saque(valor));
        contaService.atualizarConta(contaOrigem);
        contaService.atualizarConta(contaDestino);
        return transferencia;
    }
}
