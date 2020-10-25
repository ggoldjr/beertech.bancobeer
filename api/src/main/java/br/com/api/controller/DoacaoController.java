package br.com.api.controller;


import br.com.api.dto.DoacaoDto;
import br.com.api.security.UsuarioLogado;
import br.com.api.service.OperacaoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/doacoes")
public class DoacaoController {

    private final OperacaoService operacaoService;

    @Autowired
    public DoacaoController(OperacaoService operacaoService) {
        this.operacaoService = operacaoService;
    }

    @PostMapping
    @RolesAllowed({"USUARIO"})
    @ApiOperation(value = "Criar doação", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity criar(@ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado, @RequestBody DoacaoDto doacaoDto) {
        operacaoService.criarDoacao(usuarioLogado.toUsuario(), doacaoDto);
        return ResponseEntity.status(201).build();
    }
}