package com.uca.pncparcialfinalrestaurante.services.impl;


import com.uca.pncparcialfinalrestaurante.entity.Mesa;
import com.uca.pncparcialfinalrestaurante.entity.Sucursal;
import com.uca.pncparcialfinalrestaurante.exception.RecursoNoEncontradoException;
import com.uca.pncparcialfinalrestaurante.repository.MesaRepository;
import com.uca.pncparcialfinalrestaurante.repository.SucursalRepository;
import com.uca.pncparcialfinalrestaurante.services.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;
    private final SucursalRepository sucursalRepository;

    @Override
    public List<Mesa> listarPorSucursal(Long sucursalId) {
        return mesaRepository.findBySucursalId(sucursalId);
    }

    @Override
    public Mesa crear(Mesa mesa, Long sucursalId) {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada"));
        mesa.setSucursal(sucursal);
        return mesaRepository.save(mesa);
    }
}