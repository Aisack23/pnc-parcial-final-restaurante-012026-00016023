package com.uca.pncparcialfinalrestaurante.entity;

import com.uca.pncparcialfinalrestaurante.enums.EstadoMesa;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mesas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMesa estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;
}