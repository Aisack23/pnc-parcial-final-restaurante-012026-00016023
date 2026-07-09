package com.uca.pncparcialfinalrestaurante.services;

import com.uca.pncparcialfinalrestaurante.entity.Mesa;

import java.util.List;

public interface MesaService {
    List<Mesa> listarPorSucursal(Long sucursalId);
    Mesa crear(Mesa mesa, Long sucursalId);
}
