package com.uca.pncparcialfinalrestaurante.services;

import com.uca.pncparcialfinalrestaurante.dto.TokenResponseDTO;

public interface AuthService {
    TokenResponseDTO login(String username, String password);
    TokenResponseDTO refrescarToken(String refreshTokenValue);
}