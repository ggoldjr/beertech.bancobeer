package br.com.api.controller;

import br.com.api.dto.AlterarSenhaDto;
import br.com.api.dto.HabilitarOrDesabilitarDoacaoDto;
import br.com.api.dto.UsuarioDto;
import br.com.api.model.Usuario;
import br.com.api.security.UserDetailsImpl;
import br.com.api.service.ContaService;
import br.com.api.service.UsuarioService;
import br.com.api.spec.UsuarioSpec;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ContaService   contaService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, ContaService contaService) {
        this.usuarioService = usuarioService;
        this.contaService = contaService;
    }

    @PostMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Criar usuário.", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity criarUsuario(@RequestBody UsuarioSpec usuarioSpec){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.create(usuarioSpec).toUsuarioDto());
}


    @GetMapping(path="/{email}",produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Buscar usuário por e-mail.", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity buscaUsuario( @ApiParam(name="email", required=true, value="E-mail do usuário", example="teste@teste.com")
                                    @PathVariable String email){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.findByEmail(email).toUsuarioDto());

    }


    @PutMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Atualiza usuário.", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity atualizaUsuario(@RequestBody Usuario usuarioRequest){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.update(usuarioRequest).toUsuarioDto());

    }

    @GetMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Lista todos usuários.", produces="application/json")
    public ResponseEntity listaUsuarios(){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.listAll().stream().map(Usuario::toUsuarioDto).collect(Collectors.toList()));

    }

    @PatchMapping
    @ApiOperation(value="Trocar senha usuário.")
    public ResponseEntity alterarSenha(@RequestBody AlterarSenhaDto request) {
        try {
            usuarioService.updatePassword(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping(path = "/doacoes", produces = {MediaType.APPLICATION_JSON_VALUE})
    @RolesAllowed({"USUARIO"})
    public ResponseEntity usuariosQuePodemReceberDoacao(@AuthenticationPrincipal UserDetailsImpl loggedUser) {
        List<UsuarioDto> usuarioDtos = usuarioService.usuariosQuePodemReceberDoacao(loggedUser.getId()).stream()
                .map(Usuario::toUsuarioDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(usuarioDtos);
    }

    @PatchMapping(path = "/doacoes", produces = {MediaType.APPLICATION_JSON_VALUE})
    @RolesAllowed({"ADMIN"})
    public ResponseEntity podeReceberDoacao(@RequestBody HabilitarOrDesabilitarDoacaoDto habilitarOrDesabilitarDoacaoDto) {
        usuarioService.habilitarOuDesabilitarDoacao(habilitarOrDesabilitarDoacaoDto);
        return ResponseEntity.status(204).build();
    }
}