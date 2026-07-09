package com.uca.pncparcialfinalrestaurante.services;

import com.uca.pncparcialfinalrestaurante.dto.PedidoRequestDTO;
import com.uca.pncparcialfinalrestaurante.dto.PedidoResponseDTO;
import com.uca.pncparcialfinalrestaurante.security.UsuarioPrincipal;

import java.util.List;

public interface PedidoService {
    PedidoResponseDTO crear(PedidoRequestDTO dto, UsuarioPrincipal solicitante);
    PedidoResponseDTO confirmar(Long pedidoId, UsuarioPrincipal solicitante);
    PedidoResponseDTO cancelar(Long pedidoId, UsuarioPrincipal solicitante);
    List<PedidoResponseDTO> listarMisPedidos(UsuarioPrincipal solicitante);
    List<PedidoResponseDTO> listarPorSucursal(Long sucursalId, UsuarioPrincipal solicitante);
}