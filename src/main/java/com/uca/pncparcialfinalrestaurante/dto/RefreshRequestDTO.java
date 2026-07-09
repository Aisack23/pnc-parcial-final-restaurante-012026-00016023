package com.uca.pncparcialfinalrestaurante.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RefreshRequestDTO {

    @NotBlank(message = "El refreshToken es obligatorio")
    private String refreshToken;
}