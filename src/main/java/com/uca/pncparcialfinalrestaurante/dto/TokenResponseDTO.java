package com.uca.pncparcialfinalrestaurante.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tipo;
}