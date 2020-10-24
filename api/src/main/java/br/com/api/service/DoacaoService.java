package br.com.api.service;

import br.com.api.dto.ContaDto;
import br.com.api.dto.DoacaoDto;
import br.com.api.exception.DoacaoException;
import br.com.api.model.Doacao;
import br.com.api.model.Usuario;
import br.com.api.repository.DoacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final UsuarioService usuarioService;
    private final ContaService contaService;


    @Autowired
    public DoacaoService(DoacaoRepository doacaoRepository, UsuarioService usuarioService, ContaService contaService) {
        this.doacaoRepository = doacaoRepository;
        this.usuarioService = usuarioService;
        this.contaService = contaService;
    }


    public Doacao criar(Long idUsuarioDoador, DoacaoDto doacaoDto) {
        Usuario usuarioDoador = usuarioService.findById(idUsuarioDoador);
        List<ContaDto> contaDtos = contaService.listContasUsuario(usuarioDoador);
        boolean usuarioDoadorPodeDoar = usuarioDoador.podeDoar(contaDtos.get(0).getSaldo(), doacaoDto.getValorDoado().doubleValue());
        if (usuarioDoadorPodeDoar) {
            Usuario usuarioBeneficiario = usuarioService.findById(doacaoDto.getIdUsuarioBeneficiario());
            if (podeReceber(usuarioBeneficiario)) {
                Doacao doacao = Doacao.builder()
                        .dataDaDoacao(LocalDate.now())
                        .idUsuarioDoador(usuarioDoador.getId())
                        .usuario(usuarioBeneficiario)
                        .valorRecebido(doacaoDto.getValorDoado())
                        .build();
                return doacaoRepository.save(doacao);
            }
            throw new DoacaoException(String.format("Usuário %s não pode receber doação", usuarioBeneficiario.getNome()));
        }
        throw new DoacaoException(String.format("Usuário %s não pode fazer doação", usuarioDoador.getNome()));
    }

    public boolean podeReceber(Usuario usuarioBeneficiario) {
        return doacaoRepository.findAllByUsuarioId(usuarioBeneficiario.getId()).stream()
                .filter(doacao -> doacao.getDataDaDoacao().getMonthValue() == LocalDate.now().getMonthValue())
                .map(Doacao::getValorRecebido)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO).doubleValue() < 1000;
    }

    public List<Doacao> doacoesDoUsuario(Long id) {
        return doacaoRepository.findAllByAndIdUsuarioDoador(id).stream()
                .sorted(Comparator.comparing(Doacao::getDataDaDoacao))
                .collect(Collectors.toList());
    }
}
