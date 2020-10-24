package br.com.api.controller;


import br.com.api.dto.DoacaoDto;
import br.com.api.model.Doacao;
import br.com.api.security.UsuarioLogado;
import br.com.api.service.DoacaoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/doacoes")
public class DoacaoController {

    private final DoacaoService doacaoService;

    @Autowired
    public DoacaoController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    @PostMapping
    @RolesAllowed({"USUARIO"})
    @ApiOperation(value = "Criar doação", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity criar(@AuthenticationPrincipal UsuarioLogado usuarioLogado, @RequestBody DoacaoDto doacaoDto) {
        Doacao doacao = doacaoService.criar(usuarioLogado.toUsuario(), doacaoDto);
        return ResponseEntity.status(201).body(doacao);
    }
}