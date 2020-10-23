package br.com.api.controller;

import br.com.api.dto.AlterarSenhaDto;
import br.com.api.dto.UsuarioDtoIn;
import br.com.api.model.Usuario;
import br.com.api.service.ContaService;
import br.com.api.service.UsuarioService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity criarUsuario(@RequestBody UsuarioDtoIn usuarioRequest){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.create(usuarioRequest));
}


    @GetMapping(path="/{email}",produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Buscar usuário por e-mail.", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity buscaUsuario( @ApiParam(name="email", required=true, value="E-mail do usuário", example="teste@teste.com")
                                    @PathVariable String email){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.listByEmail(email));

    }


    @PutMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Atualiza usuário.", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity atualizaUsuario(@RequestBody Usuario usuarioRequest){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.update(usuarioRequest));

    }

    @GetMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Lista todos usuários.", produces="application/json")
    public ResponseEntity listaUsuarios(){

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.list());

    }

    @PatchMapping
    @ApiOperation(value="Trocar senha usuário.")
    public ResponseEntity alterarSenha(@RequestBody AlterarSenhaDto request) {

        try {
            usuarioService.updatePassword(request);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        }catch (Exception e){

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        }

    }

}