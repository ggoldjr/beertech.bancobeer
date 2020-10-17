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

@Service
public class JwtService {

    @Value("${security.jwt.tokenExpiration}")
    private String tokenExpiration;

    @Value("${security.jwt.secretKey}")
    private String base64EncodedSecretKey;

    public String generateToken(UserDetailsImpl user) {

        long expTime = Long.valueOf(tokenExpiration);

        //Take the current date and time and add the expiration date
        LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(expTime);

        System.out.println("expirationDateTime= "+ expirationDateTime);

        //Converts to the standard expected by the jwt object
        Instant instante = expirationDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date expirationDate = Date.from(instante);

        System.out.println("expirationDate= "+ expirationDate);

        //build token
        String jwt= Jwts
                .builder()
                .setSubject(encode(user))
                .setExpiration(expirationDate)
                .signWith( SignatureAlgorithm.HS512, base64EncodedSecretKey)
                .compact();

        System.out.println("jwt= "+ jwt);

        return jwt;

    }


    private Claims getClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(base64EncodedSecretKey.getBytes()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean tokenValido(String token) {
        try {
            Date dataExpiracao = getClaims(token).getExpiration();
            System.out.println("dataExpiracao " + dataExpiracao);

            LocalDateTime data = dataExpiracao.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            System.out.println("dataExpiracao " + dataExpiracao);
            System.out.println("datatual " + LocalDateTime.now());
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
        return Set.of(stringPermission.split(",")).stream()
                .map(s -> new SimpleGrantedAuthority(s.toUpperCase()))
                .collect(Collectors.toSet());
    }
    public UserDetailsImpl decode(String encoded) {
        try {
            String subject = getClaims(encoded).getSubject();
            String[] split = subject.split(":");
            return UserDetailsImpl.builder()
                    .id(Long.parseLong(split[0]))
                    .name(split[1])
                    .email(split[2])
                    .authorities(decodePermission(split[3]))
                    .build();
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }


}
