package com.uca.pncparcialfinalrestaurante.security;

import com.uca.pncparcialfinalrestaurante.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration-minutes}")
    private long accessExpirationMinutes;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarAccessToken(Usuario usuario, Long refreshTokenId) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + accessExpirationMinutes * 60 * 1000);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("userId", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .claim("sucursalId", usuario.getSucursal() != null ? usuario.getSucursal().getId() : null)
                .claim("sid", refreshTokenId)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esTokenValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }
}