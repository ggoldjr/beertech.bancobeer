package br.com.api.security;

import br.com.api.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class JwtService {

    @Value("${security.jwt.tokenExpiration}")
    private String tokenExpiration;

    @Value("${security.jwt.secretKey}")
    private String base64EncodedSecretKey;

    public String generateToken(UserDetailsImpl user) {
        long expTime = Long.parseLong(tokenExpiration);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(expTime);
        Instant instante = expirationDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date expirationDate = Date.from(instante);
        return Jwts.builder()
                .setSubject(encode(user))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, base64EncodedSecretKey)
                .compact();
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(base64EncodedSecretKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean tokenValido(String token) {
        try {
            Date dataExpiracao = getClaims(token).getExpiration();
            LocalDateTime data = dataExpiracao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !LocalDateTime.now().isAfter(data);
        } catch (Exception e) {
            return false;
        }

    }

    private String encodePermission(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
    }
    private String encode(UserDetailsImpl user) {
        return String.format("%s:%s:%s:%s", user.getId(), user.getName(), user.getEmail(), encodePermission(user.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> decodePermission(String stringPermission) {
        return Stream.of(stringPermission.split(","))
                .map(s -> new SimpleGrantedAuthority(s.toUpperCase()))
                .collect(Collectors.toSet());
    }
    public UserDetailsImpl decode(String encoded) {
        try {
            String subject = getClaims(encoded).getSubject();
            String[] split = subject.split(":");
            return UserDetailsImpl.builder()
                    .id(split[0])
                    .name(split[1])
                    .email(split[2])
                    .authorities(decodePermission(split[3]))
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }


}
