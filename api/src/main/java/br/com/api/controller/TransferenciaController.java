package br.com.api.controller;

import br.com.api.dto.TransferenciaDto;
import br.com.api.model.Transferencia;
import br.com.api.service.TransferenciaService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/transferencias")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    @Autowired
    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @PostMapping(consumes={MediaType.APPLICATION_JSON_VALUE}, produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Cira uma transação.", produces="application/json")
    public Transferencia criar(@Valid @RequestBody TransferenciaDto transferenciaDto) {
        return transferenciaService.criar(transferenciaDto);
    }
}
