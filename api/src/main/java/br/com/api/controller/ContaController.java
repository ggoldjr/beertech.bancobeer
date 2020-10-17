package br.com.api.controller;

import br.com.api.dto.OperacaoDto;
import br.com.api.dto.TransferenciaDto;
import br.com.api.model.Conta;
import br.com.api.model.Operacao;
import br.com.api.service.ContaService;
import br.com.api.service.TransferenciaService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;
    private final TransferenciaService transferenciaService;

    @Autowired
    public ContaController(ContaService contaService, TransferenciaService transferenciaService) {
        this.contaService = contaService;
        this.transferenciaService = transferenciaService;
    }

    @PostMapping
    public ResponseEntity criarConta() {
        Conta conta = contaService.criarConta();
        return ResponseEntity.status(HttpStatus.CREATED).body(conta);
    }

    @GetMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Lista as contas disponíveis.", produces="application/json")
    public List<Conta> listAll(){
        return contaService.listAll();
    }

    @PostMapping(path = "/{contaHash}/operacao",
            consumes={MediaType.APPLICATION_JSON_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Insere operação a conta.", produces="application/json")
    public ResponseEntity criarOperacao(@ApiParam(name="contaHash", required=true, value="Hash de conta", example="1")
                              @PathVariable String contaHash,
                                        @ApiParam(name="request", required=true, value="Objeto com as reservas a serem criadas/atualizadas")
                              @Valid @RequestBody OperacaoDto request){
        contaService.criarOperacao(request, contaHash);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(path = "/{contaHash}/operacoes/saques",
            consumes={MediaType.APPLICATION_JSON_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Realiza saque na conta.", produces="application/json")
    public ResponseEntity saque(@ApiParam(name="contaHash", required=true, value="Hash de conta", example="1")
                                        @PathVariable String contaHash,
                                        @ApiParam(name="request", required=true, value="Objeto com as reservas a serem criadas/atualizadas")
                                        @Valid @RequestBody OperacaoDto request){
        request.setTipo(Operacao.Tipo.SAQUE.name());
        contaService.criarOperacao(request, contaHash);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(path = "/{contaHash}/operacoes/depositos",
            consumes={MediaType.APPLICATION_JSON_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Realiza depósito na conta.", produces="application/json")
    public ResponseEntity deposito(@ApiParam(name="contaHash", required=true, value="Hash de conta", example="1")
                                        @PathVariable String contaHash,
                                        @ApiParam(name="request", required=true, value="Objeto com as reservas a serem criadas/atualizadas")
                                        @Valid @RequestBody OperacaoDto request){
        request.setTipo(Operacao.Tipo.DEPOSITO.name());
        contaService.criarOperacao(request, contaHash);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping(path = "/{contaHash}/saldos", consumes={MediaType.APPLICATION_JSON_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Retorna.", produces="application/json")
    public Double getSaldo(@ApiParam(name="contaHash", required=true, value="Hash de conta", example="1")
                             @PathVariable String contaHash){
        return contaService.getSaldo(contaHash);
    }


    @PostMapping(path = "/operacoes/tranferencias",consumes={MediaType.APPLICATION_JSON_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Cria uma transferência.", produces="application/json")
    public ResponseEntity criar(@Valid @RequestBody TransferenciaDto transferenciaDto) {
        contaService.criarOperacao(transferenciaDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
