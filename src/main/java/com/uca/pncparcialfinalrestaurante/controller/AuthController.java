package com.uca.pncparcialfinalrestaurante.controller;


import com.uca.pncparcialfinalrestaurante.dto.LoginRequestDTO;
import com.uca.pncparcialfinalrestaurante.dto.RefreshRequestDTO;
import com.uca.pncparcialfinalrestaurante.dto.TokenResponseDTO;
import com.uca.pncparcialfinalrestaurante.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto.getUsername(), dto.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO dto) {
        return ResponseEntity.ok(authService.refrescarToken(dto.getRefreshToken()));
    }
}