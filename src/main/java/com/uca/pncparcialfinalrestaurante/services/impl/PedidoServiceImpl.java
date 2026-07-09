package com.uca.pncparcialfinalrestaurante.services.impl;

import com.uca.pncparcialfinalrestaurante.dto.*;
import com.uca.pncparcialfinalrestaurante.entity.*;
import com.uca.pncparcialfinalrestaurante.enums.EstadoPedido;
import com.uca.pncparcialfinalrestaurante.exception.AccesoDenegadoSucursalException;
import com.uca.pncparcialfinalrestaurante.exception.RecursoNoEncontradoException;
import com.uca.pncparcialfinalrestaurante.repository.MesaRepository;
import com.uca.pncparcialfinalrestaurante.repository.PedidoRepository;
import com.uca.pncparcialfinalrestaurante.repository.ProductoRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.UsuarioPrincipal;
import com.uca.pncparcialfinalrestaurante.services.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public PedidoResponseDTO crear(PedidoRequestDTO dto, UsuarioPrincipal solicitante) {
        Mesa mesa = mesaRepository.findById(dto.getMesaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa no encontrada"));

        Usuario cliente = usuarioRepository.findById(solicitante.getUserId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .mesa(mesa)
                .estado(EstadoPedido.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        for (ItemPedidoRequestDTO itemDto : dto.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            ItemPedido item = ItemPedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(itemDto.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();

            pedido.getItems().add(item);
        }

        pedido = pedidoRepository.save(pedido);
        return toResponseDTO(pedido);
    }

    @Override
    @Transactional
    public PedidoResponseDTO confirmar(Long pedidoId, UsuarioPrincipal solicitante) {
        Pedido pedido = obtenerYValidarSucursal(pedidoId, solicitante);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return toResponseDTO(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public PedidoResponseDTO cancelar(Long pedidoId, UsuarioPrincipal solicitante) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        boolean esCliente = "CLIENTE".equals(solicitante.getRol());

        if (esCliente) {
            // Un cliente solo puede cancelar SUS propios pedidos.
            if (!pedido.getCliente().getId().equals(solicitante.getUserId())) {
                throw new AccesoDenegadoSucursalException("No podés cancelar pedidos de otro cliente");
            }
        } else if ("ENCARGADO".equals(solicitante.getRol())) {
            validarSucursal(pedido, solicitante);
        }
        // ADMIN: sin restricción adicional.

        pedido.setEstado(EstadoPedido.CANCELADO);
        return toResponseDTO(pedidoRepository.save(pedido));
    }

    @Override
    public List<PedidoResponseDTO> listarMisPedidos(UsuarioPrincipal solicitante) {
        return pedidoRepository.findByClienteId(solicitante.getUserId())
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDTO> listarPorSucursal(Long sucursalId, UsuarioPrincipal solicitante) {
        if ("ENCARGADO".equals(solicitante.getRol()) && !sucursalId.equals(solicitante.getSucursalId())) {
            throw new AccesoDenegadoSucursalException("No podés ver pedidos de otra sucursal");
        }
        return pedidoRepository.findByMesa_Sucursal_Id(sucursalId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // --- Requisito 2.2 / Opción B conceptual: valida que el ENCARGADO
    // solo opere pedidos de su propia sucursal. No basta con el rol. ---
    private Pedido obtenerYValidarSucursal(Long pedidoId, UsuarioPrincipal solicitante) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado"));

        if ("ENCARGADO".equals(solicitante.getRol())) {
            validarSucursal(pedido, solicitante);
        }
        return pedido;
    }

    private void validarSucursal(Pedido pedido, UsuarioPrincipal solicitante) {
        Long sucursalPedido = pedido.getSucursalId();
        if (!sucursalPedido.equals(solicitante.getSucursalId())) {
            throw new AccesoDenegadoSucursalException(
                    "El pedido pertenece a otra sucursal, no tenés permiso sobre él");
        }
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        BigDecimal total = pedido.getItems().stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ItemPedidoResponseDTO> items = pedido.getItems().stream()
                .map(i -> ItemPedidoResponseDTO.builder()
                        .productoId(i.getProducto().getId())
                        .nombreProducto(i.getProducto().getNombre())
                        .cantidad(i.getCantidad())
                        .precioUnitario(i.getPrecioUnitario())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .clienteId(pedido.getCliente().getId())
                .mesaId(pedido.getMesa().getId())
                .sucursalId(pedido.getSucursalId())
                .estado(pedido.getEstado())
                .fechaCreacion(pedido.getFechaCreacion())
                .total(total)
                .items(items)
                .build();
    }
}