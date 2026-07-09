package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.entity.Mesa;
import com.uca.pncparcialfinalrestaurante.services.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sucursales/{sucursalId}/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public ResponseEntity<List<Mesa>> listar(@PathVariable Long sucursalId) {
        return ResponseEntity.ok(mesaService.listarPorSucursal(sucursalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENCARGADO')")
    public ResponseEntity<Mesa> crear(@PathVariable Long sucursalId, @RequestBody Mesa mesa) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaService.crear(mesa, sucursalId));
    }
}