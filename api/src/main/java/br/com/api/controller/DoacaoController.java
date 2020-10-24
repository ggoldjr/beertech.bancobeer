package br.com.api.controller;


import br.com.api.dto.DoacaoDto;
import br.com.api.model.Doacao;
import br.com.api.security.UserDetailsImpl;
import br.com.api.service.DoacaoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

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
    public ResponseEntity criar(@AuthenticationPrincipal UserDetailsImpl loggedUser, @RequestBody DoacaoDto doacaoDto) {
        Doacao doacao = doacaoService.criar(loggedUser.getId(), doacaoDto);
        return ResponseEntity.status(201).body(doacao);
    }

    @GetMapping(path = "/me")
    @RolesAllowed({"USUARIO"})
    @ApiOperation(value = "Listar doações do usuário logado.", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Doacao> doacoesDoUsuario(@AuthenticationPrincipal UserDetailsImpl loggedUser) {
        return doacaoService.doacoesDoUsuario(loggedUser.getId());
    }
}