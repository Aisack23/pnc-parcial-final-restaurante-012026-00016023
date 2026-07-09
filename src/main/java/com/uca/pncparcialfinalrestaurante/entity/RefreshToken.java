package com.uca.pncparcialfinalrestaurante.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    // Se actualiza en CADA request autenticado del usuario aqui es donde añadi lo de la C sin importar el token

    @Column(nullable = false)
    private LocalDateTime ultimaActividad;

    @Column(nullable = false)
    @Builder.Default
    private boolean revocado = false;
}