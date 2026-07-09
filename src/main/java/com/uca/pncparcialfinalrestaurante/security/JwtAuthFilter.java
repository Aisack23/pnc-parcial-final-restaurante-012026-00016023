package com.uca.pncparcialfinalrestaurante.security;

import com.uca.pncparcialfinalrestaurante.repository.RefreshTokenRepository;
import com.uca.pncparcialfinalrestaurante.entity.RefreshToken;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.inactivity-minutes}")
    private long inactivityMinutes;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtUtil.esTokenValido(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtUtil.extraerClaims(token);
        String rol = claims.get("rol", String.class);
        Long sid = claims.get("sid", Long.class);

        // Regla de negocio (Opción C): solo aplica control de inactividad
        // a sesiones de ENCARGADO. Admin y Cliente se rigen solo por la
        // expiración normal del JWT.
        if ("ENCARGADO".equals(rol) && sid != null) {
            Optional<RefreshToken> rtOpt = refreshTokenRepository.findById(sid);

            if (rtOpt.isEmpty() || rtOpt.get().isRevocado()) {
                filterChain.doFilter(request, response); // queda sin autenticar -> 401/403 más adelante
                return;
            }

            RefreshToken rt = rtOpt.get();
            long minutosInactivo = ChronoUnit.MINUTES.between(rt.getUltimaActividad(), LocalDateTime.now());

            if (minutosInactivo > inactivityMinutes) {
                rt.setRevocado(true);
                refreshTokenRepository.save(rt);
                filterChain.doFilter(request, response); // sesión muerta, no se autentica
                return;
            }

            // actividad válida: deslizamos la ventana
            rt.setUltimaActividad(LocalDateTime.now());
            refreshTokenRepository.save(rt);
        }

        String username = jwtUtil.extraerUsername(token);
        var userDetails = userDetailsService.loadUserByUsername(username);

        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}