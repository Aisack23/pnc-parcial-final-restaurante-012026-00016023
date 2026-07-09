package com.uca.pncparcialfinalrestaurante.dto;

import com.uca.pncparcialfinalrestaurante.enums.EstadoPedido;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoResponseDTO {
    private Long id;
    private Long clienteId;
    private Long mesaId;
    private Long sucursalId;
    private EstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private BigDecimal total;
    private List<ItemPedidoResponseDTO> items;
}