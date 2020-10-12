package br.com.api.controller;

import br.com.api.dto.OperacaoDto;
import br.com.api.model.Conta;
import br.com.api.service.ContaService;
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

    @Autowired
    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    public ResponseEntity criarConta() {
        contaService.criarConta();
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    @GetMapping(path = "/{contaHash}/saldos", consumes={MediaType.APPLICATION_JSON_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Retorna.", produces="application/json")
    public Double getSaldo(@ApiParam(name="contaHash", required=true, value="Hash de conta", example="1")
                             @PathVariable String contaHash){
        return contaService.getSaldo(contaHash);
    }

}
