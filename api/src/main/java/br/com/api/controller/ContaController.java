package br.com.api.controller;

import br.com.api.dto.OperacaoDto;
import br.com.api.model.Operacao;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import br.com.api.model.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import br.com.api.service.ContaService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    @Autowired
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    public Conta criarConta() {
        return contaService.criarConta();
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
    public Operacao criarOperacao(@ApiParam(name="contaHash", required=true, value="Hash de conta", example="1")
                              @PathVariable String contaHash,
                              @ApiParam(name="request", required=true, value="Objeto com as reservas a serem criadas/atualizadas")
                              @Valid @RequestBody OperacaoDto request){
        return contaService.criarOperacao(request, contaHash);
    }
    @GetMapping(path = "/{contaHash}/saldos", consumes={MediaType.APPLICATION_JSON_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Retorna.", produces="application/json")
    public Double getSaldo(@ApiParam(name="contaHash", required=true, value="Hash de conta", example="1")
                             @PathVariable String contaHash){
        return contaService.getSaldo(contaHash);
    }

}
