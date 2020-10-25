package br.com.api.controller;

import br.com.api.dto.AlterarSenhaDto;
import br.com.api.dto.HabilitarOrDesabilitarDoacaoDto;
import br.com.api.dto.UsuarioDto;
import br.com.api.model.Usuario;
import br.com.api.security.UsuarioLogado;
import br.com.api.service.UsuarioService;
import br.com.api.spec.AtualizarUsuarioSpec;
import br.com.api.spec.UsuarioSpec;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Criar usuário.", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity criarUsuario(@Valid @RequestBody UsuarioSpec usuarioSpec){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.criar(usuarioSpec).toUsuarioDto());
    }

    @GetMapping(path="/{email}",produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Buscar usuário por e-mail.", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity buscaUsuarioPorEmail(@ApiParam(name="email", required=true, value="E-mail do usuário", example="teste@teste.com")
                                               @PathVariable String email,
                                               @ApiIgnore@AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.buscarPorEmail(email, usuarioLogado.toUsuario()).toUsuarioDto());
    }

    @Secured("USUARIO")
    @PutMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Atualiza usuário.", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity atualizaUsuario(@Valid @RequestBody AtualizarUsuarioSpec atualizarUsuarioSpec,
                                          @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.update(atualizarUsuarioSpec, usuarioLogado.toUsuario()).toUsuarioDto());
    }

    @Secured({"ADMIN", "USUARIO"})
    @GetMapping(path = "/all", produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Lista todos usuários.", produces="application/json")
    public ResponseEntity listaUsuarios(@ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuarioService.listAll(usuarioLogado.toUsuario()).stream()
                        .map(Usuario::toUsuarioDto)
                        .collect(Collectors.toList()));
    }

    @Secured({"USUARIO", "ADMIN"})
    @PatchMapping
    @ApiOperation(value="Trocar senha usuário.")
    public ResponseEntity alterarSenha(@Valid @RequestBody AlterarSenhaDto request,
                                       @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        usuarioService.updatePassword(request, usuarioLogado.toUsuario());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Secured({"USUARIO"})
    public ResponseEntity usuariosQuePodemReceberDoacao(@ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado,
                                                        @RequestParam(defaultValue = "") String podeReceberDoacao,
                                                        @ApiIgnore @RequestParam(defaultValue = "") String minhasDoacoes,
                                                        @RequestParam(defaultValue = "") String semDoacoes) {
        List<UsuarioDto> usuarioDtos = usuarioService.listaUsuarios(usuarioLogado.toUsuario(), podeReceberDoacao, minhasDoacoes, semDoacoes).stream()
                .map(Usuario::toUsuarioDto)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(usuarioDtos);
    }

    @PatchMapping(path = "/doacoes", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Secured({"ADMIN"})
    public ResponseEntity atualizarCampoPodeReceberDoacao(@Valid @RequestBody HabilitarOrDesabilitarDoacaoDto habilitarOrDesabilitarDoacaoDto,
                                                          @ApiIgnore @AuthenticationPrincipal UsuarioLogado usuarioLogado) {
        usuarioService.atualizarCampoPodeReceberDoacao(habilitarOrDesabilitarDoacaoDto, usuarioLogado.toUsuario());
        return ResponseEntity.status(204).build();
    }
}