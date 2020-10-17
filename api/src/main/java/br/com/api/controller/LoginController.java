package br.com.api.controller;

import br.com.api.dto.TokenDto;
import br.com.api.exception.SenhaInvalidaException;
import br.com.api.model.Usuario;
import br.com.api.security.JwtService;
import br.com.api.security.UserDetailsImpl;
import br.com.api.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public LoginController(JwtService jwtService, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtService = jwtService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value="Login de usuário.", produces="application/json")
    public ResponseEntity autenticar(HttpServletRequest request,
                                     HttpServletResponse response,
                                     Authentication authResult) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        response.setStatus(200);
        response.addHeader("Authorization", String.format("Bearer %s", token));
        UserDetails user = userDetailsServiceImpl.loadUserByUsername(userDetails.getEmail());
        response.getWriter().append(new ObjectMapper().writeValueAsString(user));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
