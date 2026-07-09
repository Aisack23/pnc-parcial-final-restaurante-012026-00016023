package com.uca.pncparcialfinalrestaurante.services.impl;


import com.uca.pncparcialfinalrestaurante.dto.TokenResponseDTO;
import com.uca.pncparcialfinalrestaurante.entity.RefreshToken;
import com.uca.pncparcialfinalrestaurante.entity.Usuario;
import com.uca.pncparcialfinalrestaurante.exception.CredencialesInvalidasException;
import com.uca.pncparcialfinalrestaurante.exception.SesionInactivaException;
import com.uca.pncparcialfinalrestaurante.repository.RefreshTokenRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.JwtUtil;
import com.uca.pncparcialfinalrestaurante.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-token-expiration-days}")
    private long refreshExpirationDays;

    @Value("${jwt.inactivity-minutes}")
    private long inactivityMinutes;

    @Override
    @Transactional
    public TokenResponseDTO login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
        }

        LocalDateTime ahora = LocalDateTime.now();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .fechaExpiracion(ahora.plusDays(refreshExpirationDays))
                .ultimaActividad(ahora)
                .revocado(false)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        String accessToken = jwtUtil.generarAccessToken(usuario, refreshToken.getId());

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tipo("Bearer")
                .build();
    }

    @Override
    @Transactional
    public TokenResponseDTO refrescarToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new CredencialesInvalidasException("Refresh token inválido"));

        if (refreshToken.isRevocado()) {
            throw new SesionInactivaException("El token fue revocado");
        }

        if (refreshToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new CredencialesInvalidasException("Refresh token expirado");
        }

        // Regla de negocio Opción C: expiración forzada por inactividad,
        // aplicada explícitamente también en el flujo de refresh, no solo
        // en el filtro de requests normales.
        Usuario usuario = refreshToken.getUsuario();
        if (usuario.getRol().name().equals("ENCARGADO")) {
            long minutosInactivo = ChronoUnit.MINUTES.between(
                    refreshToken.getUltimaActividad(), LocalDateTime.now());

            if (minutosInactivo > inactivityMinutes) {
                refreshToken.setRevocado(true);
                refreshTokenRepository.save(refreshToken);
                throw new SesionInactivaException(
                        "Sesión expirada por inactividad (" + minutosInactivo + " min sin actividad)");
            }
        }

        refreshToken.setUltimaActividad(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);

        String nuevoAccessToken = jwtUtil.generarAccessToken(usuario, refreshToken.getId());

        return TokenResponseDTO.builder()
                .accessToken(nuevoAccessToken)
                .refreshToken(refreshToken.getToken())
                .tipo("Bearer")
                .build();
    }
}