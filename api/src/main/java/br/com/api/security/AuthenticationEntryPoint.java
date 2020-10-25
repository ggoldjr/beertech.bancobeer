package br.com.api.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint{

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
                throws IOException {
            response.addHeader("WWW-Authenticate", "Basic realm=" +getRealmName());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().append(json());
        }

        private String json() {
            long date = new Date().getTime();
            return "{  \"timestamp\": " + date + ", "
                    + "\"status\": 401, "
                    + "\"error\": \"Não autorizado\", "
                    + "\"message\": \"Email ou senha inválido\", "
                    + "\"path\": \"/login\" }";
        }

        @Override
        public void afterPropertiesSet() {
            setRealmName("DeveloperStack");
            super.afterPropertiesSet();
        }
}