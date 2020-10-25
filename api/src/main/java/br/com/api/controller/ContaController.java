package br.com.api.controller;

import br.com.api.dto.OperacaoDto;
import br.com.api.dto.TransferenciaDto;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.security.UsuarioLogado;
import br.com.api.service.ContaService;
import br.com.api.service.OperacaoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;
    private final OperacaoService operacaoService;

    @Autowired
    public ContaController(ContaService contaService, OperacaoService operacaoService) {
        this.contaService = contaService;
        this.operacaoService = operacaoService;
    }

    @Secured("ADMIN")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Lista as contas disponíveis.", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorno da lista de contas executado com sucesso..."),
            @ApiResponse(code = 401, message = "Você não tem autorização para acessar este recurso"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 404, message = "Conta não encontrada"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    public ResponseEntity listAll() {
        return ResponseEntity.status(HttpStatus.OK).body(contaService.listAll().stream()
                .map(Conta::toContaDto)
                .collect(Collectors.toList()));
    }

    @Secured({"ADMIN", "USUARIO"})
    @GetMapping(path = "/{contaHash}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Lista conta por hash ou id.", produces = "application/json")
    public ResponseEntity getContaByHash(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                         @PathVariable String contaHash,
                                         @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        return ResponseEntity.status(HttpStatus.OK).body(contaService.findByHash(contaHash, usuarioLogado.toUsuario()).toContaDto());
    }

    @Secured({"ADMIN", "USUARIO"})
    @GetMapping(path = "/id/{contaId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Lista conta por id.", produces = "application/json")
    public ResponseEntity getContaById(@ApiParam(name = "contaId", required = true, value = "Id da conta", example = "1")
                                       @PathVariable Long contaId,
                                       @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        return ResponseEntity.status(HttpStatus.OK).body(contaService.findById(contaId, usuarioLogado.toUsuario()).toContaDto());
    }

    @Secured({"ADMIN", "USUARIO"})
    @GetMapping(path = "/{contaHash}/extratos",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Obter extrato da conta pelo hash.", produces = "application/json")
    public ResponseEntity getExtratoConta(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                          @PathVariable String contaHash,
                                          @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        return ResponseEntity.status(HttpStatus.OK).body(operacaoService.getExtrato(contaHash, usuarioLogado.toUsuario()));
    }

    @Secured("ADMIN")
    @PostMapping(path = "/{contaHash}/operacoes/depositos",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Realiza depósito na conta.", produces = "application/json")
    public ResponseEntity deposito(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                   @PathVariable String contaHash,
                                   @ApiParam(name = "request", required = true, value = "Objeto com as reservas a serem criadas/atualizadas")
                                   @Valid @RequestBody OperacaoDto request) {
        request.setTipo(Operacao.Tipo.DEPOSITO.name());
        operacaoService.deposito(contaHash, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured({"USUARIO", "ADMIN"})
    @GetMapping(path = "/{contaHash}/saldos")
    @ApiOperation(value = "Retorna saldo da conta.")
    public ResponseEntity getSaldo(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                   @PathVariable String contaHash,
                                   @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        return ResponseEntity.status(HttpStatus.OK).body(contaService.getSaldo(contaHash, usuarioLogado.toUsuario()));
    }

    @Secured("USUARIO")
    @PostMapping(path = "/operacoes/tranferencias", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Cria uma transferência.", produces = "application/json")
    public ResponseEntity criarTransferencia(@Valid @RequestBody TransferenciaDto transferenciaDto,
                                             @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        operacaoService.criarTransferencia(transferenciaDto, usuarioLogado.toUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Secured({ "ADMIN"})
    @GetMapping(path = "/depositos")
    @ApiOperation(value = "Retorna todos os depositos.")
    public ResponseEntity getDepositos( @RequestParam(defaultValue = "") String contaHash,
                                        @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        return ResponseEntity.status(HttpStatus.OK).body(operacaoService.getDepositos(contaHash));
    }
}