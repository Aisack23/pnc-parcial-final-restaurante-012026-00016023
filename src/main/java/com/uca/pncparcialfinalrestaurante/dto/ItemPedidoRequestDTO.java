package com.uca.pncparcialfinalrestaurante.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ItemPedidoRequestDTO {

    @NotNull
    private Long productoId;

    @Min(1)
    private Integer cantidad;
}