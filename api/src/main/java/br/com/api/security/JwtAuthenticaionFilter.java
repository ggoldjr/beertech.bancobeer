package br.com.api.security;



import br.com.api.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthenticaionFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtAuthenticaionFilter( JwtService jwtService, UserDetailsServiceImpl userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            if (request.getRequestURI().contains("/login") &&
                    request.getMethod().equalsIgnoreCase("GET")) {

                String authorization = request.getHeader("Authorization");

                if (authorization != null && authorization.startsWith("Basic")) {
                    super.doFilter(request,response,filterChain);
                } else {
                    filterChain.doFilter(request, response);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected void onSuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Authentication authResult) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        response.setStatus(200);
        response.addHeader("Authorization", String.format("Bearer %s", token));
        UserDetails user = userDetailsService.loadUserByUsername(userDetails.getEmail());
        response.getWriter().append(new ObjectMapper().writeValueAsString(user));

    }
}
