package br.com.api.controller;

import br.com.api.dto.UsuarioDto;
import br.com.api.model.Usuario;
import br.com.api.service.UsuarioService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Criar usuário.", produces="application/json")
    public ResponseEntity criarUsuario(@RequestBody Usuario usuarioRequest){

        Usuario usuario = usuarioService.save(usuarioRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                UsuarioDto.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .cnpj(usuario.getCnpj())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil()).build());


    }

    @GetMapping(path="/{email}",produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Buscar usuário por e-mail.", produces="application/json")
    public UsuarioDto buscaUsuario( @ApiParam(name="email", required=true, value="E-mail do usuário", example="teste@teste.com")
                                    @PathVariable String email){

        Usuario usuario = usuarioService.findByEmail(email);

        return UsuarioDto.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .cnpj(usuario.getCnpj())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil()).build()
                ;

    }


    @PutMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Atualiza usuário.", produces="application/json")
    public UsuarioDto atualizaUsuario(@RequestBody Usuario usuarioRequest){

        Usuario usuario = usuarioService.update(usuarioRequest);

        return UsuarioDto.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .cnpj(usuario.getCnpj())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil()).build()
                ;

    }

    @GetMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Lista todos usuários.", produces="application/json")
    public List<UsuarioDto> listaUsuarios(){

        return new ArrayList<UsuarioDto>();

    }




}
