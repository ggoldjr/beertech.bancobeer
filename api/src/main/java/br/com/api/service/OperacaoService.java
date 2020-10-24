package br.com.api.service;

import br.com.api.dto.DoacaoDto;
import br.com.api.dto.ExtratoDto;
import br.com.api.dto.OperacaoDto;
import br.com.api.dto.TransferenciaDto;
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
import java.time.MonthDay;
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
        return operacaoRepository.save(operacao);
    }

    public List<ExtratoDto> getExtrato(String contaHash, Usuario usuario) {
        if (!usuario.eAdmin() && !contaHash.equals(usuario.getContaHash())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), "Não pode ver extrato de outras contas");
        }
        return operacaoRepository.findByHashContaDestinoOrConta_Hash(contaHash).stream()
                .map(operacao -> {
                    ExtratoDto.ExtratoDtoBuilder extratoDtoBuilder = ExtratoDto.builder()
                            .valor(operacao.getValor().doubleValue())
                            .data(operacao.getCriado_em())
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
        Double valor = transferenciaDto.getValor().doubleValue();
        if (!contaOrigem.saldoEmaiorOrIgualA(valor)) throw new SaldoInsuficienteException();
        Conta contaDestino = contaService.findByHash(transferenciaDto.getHashContaDestino(), usuario);
        Operacao operacao = Operacao.builder()
                .conta(contaOrigem)
                .hashContaDestino(contaDestino.getHash())
                .valor(transferenciaDto.getValor())
                .tipo(Operacao.Tipo.TRANSFERENCIA)
                .build();
        operacao = operacaoRepository.save(operacao);
        contaDestino.deposito(contaOrigem.saque(valor, contaOrigem.getUsuario()));
        contaService.atualizarConta(contaOrigem);
        contaService.atualizarConta(contaDestino);
        return operacao;
    }

    public Operacao criarDoacao(Usuario usuarioLogado, DoacaoDto doacaoDto) {
        Usuario usuarioDoador = usuarioRepository.findById(usuarioLogado.getId()).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
        List<Conta> contas = contaService.listContasUsuario(usuarioDoador);
        boolean usuarioDoadorPodeDoar = usuarioDoador.podeDoar(contas.get(0).getSaldo(), doacaoDto.getValorDoado().doubleValue());
        if (usuarioDoadorPodeDoar) {
            Usuario usuarioBeneficiario = usuarioRepository.findById(doacaoDto.getIdUsuarioBeneficiario()).orElseThrow(() -> new NotFoundException("Usuário não encontrado "));
            if (podeReceber(usuarioBeneficiario)) {
                Operacao operacao = Operacao.builder()
                        .conta(contas.get(0))
                        .tipo(Operacao.Tipo.DOACAO)
                        .hashContaDestino(usuarioBeneficiario.getContaHash())
                        .valor(doacaoDto.getValorDoado())
                        .dataOperacao(LocalDate.now())
                        .build();
                Operacao operacaoCriada = operacaoRepository.save(operacao);
                Conta contaUsuarioDoador = contas.get(0);
                contaUsuarioDoador.saque(doacaoDto.getValorDoado().doubleValue(), usuarioDoador);
                Conta contaUsuarioBeneficiario = contaService.findByHash(usuarioBeneficiario.getContaHash());
                contaUsuarioBeneficiario.deposito(doacaoDto.getValorDoado().doubleValue());
                contaService.atualizarConta(contaUsuarioDoador);
                contaService.atualizarConta(contaUsuarioBeneficiario);
                return operacaoCriada;
            }
            throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), String.format("Usuário %s não pode receber doação", usuarioBeneficiario.getNome()));
        }
        throw new ApplicationException(HttpStatus.UNAUTHORIZED.value(), String.format("Usuário %s não pode fazer doação", usuarioDoador.getNome()));
    }

    public boolean podeReceber(Usuario usuarioBeneficiario) {
        return operacaoRepository.findAllByhashContaDestino(usuarioBeneficiario.getContaHash()).stream()
                .filter(o -> o.getDataOperacao().getMonthValue() == LocalDate.now().getMonthValue())
                .map(operacao -> operacao.getValor())
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO).doubleValue() < 1000 && usuarioBeneficiario.getPodeReceberDoacoes();
    }

    public List<Operacao> findAllByAndHashUsuarioDoador(String hashContaDoador) {
        return operacaoRepository.findAllByConta_Hash(hashContaDoador);
    }

    public List<Operacao> doacoesDoMes() {
        return operacaoRepository.findAllByDataOperacaoIsBetween(LocalDate.now().withDayOfMonth(1),
                LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())).stream()
                .filter(operacao -> operacao.getTipo() == Operacao.Tipo.DOACAO)
                .collect(Collectors.toList());
    }
}
