package br.com.api.security;



import br.com.api.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthenticaionFilter extends BasicAuthenticationFilter {


    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtAuthenticaionFilter(AuthenticationManager authenticationManager,
                                  JwtService jwtService,
                                  UserDetailsServiceImpl userDetailsService,
                                  AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager, authenticationEntryPoint);
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            if (request.getRequestURI().contains("/login") && request.getMethod().equalsIgnoreCase("GET")) {
                String authorization = request.getHeader("Authorization");
                if (authorization != null && authorization.startsWith("Basic")) {
                    super.doFilterInternal(request, response, filterChain);
                } else {
                    filterChain.doFilter(request, response);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Authentication authResult) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        response.setStatus(200);
        response.getWriter().append(token);

    }
}
