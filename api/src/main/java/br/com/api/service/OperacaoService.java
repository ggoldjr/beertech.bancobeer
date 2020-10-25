package br.com.api.service;

import br.com.api.dto.DoacaoDto;
import br.com.api.dto.ExtratoDto;
import br.com.api.dto.OperacaoDto;
import br.com.api.dto.TransferenciaDto;
import br.com.api.exception.ApplicationException;
import br.com.api.exception.NotFoundException;
import br.com.api.exception.SaldoInsuficienteException;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.model.Usuario;
import br.com.api.repository.OperacaoRepository;
import br.com.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperacaoService {

    private final OperacaoRepository operacaoRepository;
    private final ContaService contaService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public OperacaoService(OperacaoRepository operacaoRepository, ContaService contaService, UsuarioRepository usuarioRepository) {
        this.operacaoRepository = operacaoRepository;
        this.contaService = contaService;
        this.usuarioRepository = usuarioRepository;
    }

    public Operacao deposito(String contaHash, OperacaoDto operacaoDto) {
        Conta conta = contaService.findByHash(contaHash);
        Operacao operacao = Operacao.criar(operacaoDto, conta);
        conta.deposito(operacao.getValor().doubleValue());
        Operacao operacaoCriada = operacaoRepository.save(operacao);
        contaService.atualizarConta(conta);
        return operacaoCriada;
    }

    public List<ExtratoDto> getExtrato(String contaHash, Usuario usuario) {
        if (!usuario.eAdmin() && !contaHash.equals(usuario.getContaHash())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Não pode ver extrato de outras contas");
        }
        return operacaoRepository.findByHashContaDestinoOrConta_Hash(contaHash).stream()
                .map(operacao -> {
                    ExtratoDto.ExtratoDtoBuilder extratoDtoBuilder = ExtratoDto.builder()
                            .valor(operacao.getValor().doubleValue())
                            .data(operacao.getCriado_em());

                    if (operacao.tipoEtransferenciaOUdoacao()) {
                        extratoDtoBuilder.hashContaDestino(operacao.getHashContaDestino() == null ? "" : operacao.getHashContaDestino());
                        extratoDtoBuilder.nomeUsuarioDestino(contaService.findNomeUsuarioByHash(operacao.getHashContaDestino()));
                        extratoDtoBuilder.hashContaOrigem(operacao.getConta().getHash());
                        extratoDtoBuilder.nomeUsuarioOrigem(operacao.getConta().getUsuario().getNome());
                    }

                    if (operacao.tipoEtransferenciaOUdoacao() &&
                            operacao.getHashContaDestino().equals(contaHash)) {

                        extratoDtoBuilder.tipo(Operacao.Tipo.TRANSFERENCIA_RECEBIDA.name());


                    } else {
                        extratoDtoBuilder.tipo(operacao.getTipo().name());
                    }



                    return extratoDtoBuilder.build();
                }).collect(Collectors.toList());
    }

    public Operacao criarTransferencia(TransferenciaDto transferenciaDto, Usuario usuario) {
        Conta contaOrigem =  contaService.findByHash(transferenciaDto.getHashContaOrigem());
        if (contaOrigem.getUsuario().getId().longValue() != usuario.getId().longValue()) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Só pode transferir dinheiro da sua conta");
        }
        if (transferenciaDto.getHashContaDestino().equals(contaOrigem.getHash())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Só pode transferir dinheiro para você mesmo");
        }
        Double valor = transferenciaDto.getValor().doubleValue();
        if (!contaOrigem.saldoEmaiorOrIgualA(valor)) throw new SaldoInsuficienteException();
        Conta contaDestino = contaService.findByHash(transferenciaDto.getHashContaDestino());
        Operacao operacao = Operacao.builder()
                .conta(contaOrigem)
                .hashContaDestino(contaDestino.getHash())
                .valor(transferenciaDto.getValor())
                .tipo(Operacao.Tipo.TRANSFERENCIA)
                .dataOperacao(LocalDate.now())
                .build();
        operacao = operacaoRepository.save(operacao);
        contaDestino.deposito(contaOrigem.saque(valor, contaOrigem.getUsuario()));
        contaService.atualizarConta(contaOrigem);
        contaService.atualizarConta(contaDestino);
        return operacao;
    }

    public Operacao criarDoacao(Usuario usuarioLogado, DoacaoDto doacaoDto) {
        Usuario usuarioDoador = usuarioRepository.findById(usuarioLogado.getId()).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
        Conta conta = contaService.listContaUsuario(usuarioDoador);
        boolean usuarioDoadorPodeDoar = usuarioDoador.podeDoar(conta.getSaldo(), doacaoDto.getValorDoado().doubleValue());
        if (usuarioDoadorPodeDoar) {
            Usuario usuarioBeneficiario = usuarioRepository.findById(doacaoDto.getIdUsuarioBeneficiario()).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
            if (podeReceber(usuarioBeneficiario)) {
                Operacao operacao = Operacao.builder()
                        .conta(conta)
                        .tipo(Operacao.Tipo.DOACAO)
                        .hashContaDestino(usuarioBeneficiario.getContaHash())
                        .valor(doacaoDto.getValorDoado())
                        .dataOperacao(LocalDate.now())
                        .build();
                Operacao operacaoCriada = operacaoRepository.save(operacao);
                Conta contaUsuarioDoador = conta;
                contaUsuarioDoador.saque(doacaoDto.getValorDoado().doubleValue(), usuarioDoador);
                Conta contaUsuarioBeneficiario = contaService.findByHash(usuarioBeneficiario.getContaHash());
                contaUsuarioBeneficiario.deposito(doacaoDto.getValorDoado().doubleValue());
                contaService.atualizarConta(contaUsuarioDoador);
                contaService.atualizarConta(contaUsuarioBeneficiario);
                if (atingiuLimiteMensalDedoacoes(usuarioBeneficiario)) {
                    usuarioBeneficiario.setPodeReceberDoacoes(false);
                    usuarioRepository.save(usuarioBeneficiario);
                }
                return operacaoCriada;
            }
            usuarioBeneficiario.setPodeReceberDoacoes(false);
            usuarioRepository.save(usuarioBeneficiario);
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), String.format("Usuário %s não pode receber doação", usuarioBeneficiario.getNome()));
        }
        throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), String.format("Usuário %s não pode fazer doação", usuarioDoador.getNome()));
    }

    public boolean podeReceber(Usuario usuarioBeneficiario) {
        return operacaoRepository.findAllByhashContaDestino(usuarioBeneficiario.getContaHash()).stream()
                .filter(o -> o.getDataOperacao().getMonthValue() == LocalDate.now().getMonthValue())
                .map(Operacao::getValor)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO).doubleValue() < 1000 && usuarioBeneficiario.getPodeReceberDoacoes();
    }

    public boolean atingiuLimiteMensalDedoacoes(Usuario usuarioBeneficiario) {
        return operacaoRepository.findAllByhashContaDestino(usuarioBeneficiario.getContaHash()).stream()
                .filter(o -> o.getDataOperacao().getMonthValue() == LocalDate.now().getMonthValue())
                .map(Operacao::getValor)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO).doubleValue() >= 1000 && usuarioBeneficiario.getPodeReceberDoacoes();
    }

    public List<Operacao> findAllByContaHash(String hashContaDoador) {
        return operacaoRepository.findAllByConta_Hash(hashContaDoador);
    }

    public List<Operacao> doacoesDoMes() {
        return operacaoRepository.findAllByDataOperacaoIsBetween(LocalDate.now().withDayOfMonth(1),
                LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())).stream()
                .filter(operacao -> operacao.getTipo() == Operacao.Tipo.DOACAO)
                .collect(Collectors.toList());
    }

    public List<Operacao> findAllByContaDestinoHash(String hash) {
        return operacaoRepository.findAllByHashContaDestino(hash);
    }

    public Object getDepositos(String contaHash) {
        if (contaHash.isEmpty()){
            return operacaoRepository.findByTipo(Operacao.Tipo.DEPOSITO);
        }

        Conta conta = contaService.findByHash(contaHash);

        List<Operacao> operacoes = operacaoRepository.findAllByConta_IdAndTipo(conta.getId(), Operacao.Tipo.DEPOSITO);

        return operacoes;
    }
}