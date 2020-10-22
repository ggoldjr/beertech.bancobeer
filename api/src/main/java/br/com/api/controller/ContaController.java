package br.com.api.controller;

import br.com.api.dto.*;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.service.ContaService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    @Autowired
    public ContaController(ContaService contaService) {
        this.contaService = contaService;

    }

    @PostMapping
    @ApiOperation(value = "Criar conta para o usuário.",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity criarConta(@Valid @RequestBody ContaDtoIn request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contaService.create(request));
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Lista as contas disponíveis.", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity listAll() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contaService.list());

    }

    @RolesAllowed({"ADMIN"})
    @GetMapping(path = "/{contaHash}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Lista conta por hash ou id.", produces = "application/json")
    public ResponseEntity getContaByHash(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                      @PathVariable String contaHash

    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contaService.listByHash(contaHash));

    }

    @RolesAllowed({"ADMIN"})
    @GetMapping(path = "/id/{contaId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Lista conta por id.", produces = "application/json")
    public ResponseEntity getContaById(@ApiParam(name = "contaId", required = true, value = "Id da conta", example = "1")
                                    @PathVariable Long contaId

    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contaService.listById(contaId));
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping(path = "/extratos/{contaHash}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Obter extrato da conta pelo hash.", produces = "application/json")
    public ResponseEntity getExtratoConta(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                            @PathVariable String contaHash

    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contaService.listaOperacoesByHash(contaHash));
    }


    @PostMapping(path = "/{contaHash}/operacoes",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Realiza saque na conta.", produces = "application/json")
    public ResponseEntity operacao(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                   @PathVariable String contaHash,
                                   @ApiParam(name = "request", required = true, value = "Objeto com as reservas a serem criadas/atualizadas")
                                   @Valid @RequestBody OperacaoDto request) {
        contaService.criarOperacao(request, contaHash);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping(path = "/{contaHash}/operacoes/saques",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Realiza saque na conta.", produces = "application/json")
    public ResponseEntity saque(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                @PathVariable String contaHash,
                                @ApiParam(name = "request", required = true, value = "Objeto com as reservas a serem criadas/atualizadas")
                                @Valid @RequestBody OperacaoDto request) {
        request.setTipo(Operacao.Tipo.SAQUE.name());
        contaService.criarOperacao(request, contaHash);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(path = "/{contaHash}/operacoes/depositos",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Realiza depósito na conta.", produces = "application/json")
    public ResponseEntity deposito(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                                   @PathVariable String contaHash,
                                   @ApiParam(name = "request", required = true, value = "Objeto com as reservas a serem criadas/atualizadas")
                                   @Valid @RequestBody OperacaoDto request) {
        request.setTipo(Operacao.Tipo.DEPOSITO.name());
        contaService.criarOperacao(request, contaHash);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping(path = "/{contaHash}/saldos")
    @ApiOperation(value = "Retorna saldo da conta.")
    public ResponseEntity getSaldo(@ApiParam(name = "contaHash", required = true, value = "Hash de conta", example = "1")
                             @PathVariable String contaHash) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contaService.listSaldo(contaHash));



    }


    @PostMapping(path = "/operacoes/tranferencias", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Cria uma transferência.", produces = "application/json")
    public ResponseEntity criar(@Valid @RequestBody TransferenciaDto transferenciaDto) {
        contaService.criarOperacao(transferenciaDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
