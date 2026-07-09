package com.uca.pncparcialfinalrestaurante.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PedidoRequestDTO {

    @NotNull
    private Long mesaId;

    @NotEmpty(message = "El pedido debe tener al menos un item")
    @Valid
    private List<ItemPedidoRequestDTO> items;
}