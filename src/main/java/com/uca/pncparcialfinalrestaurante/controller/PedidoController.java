package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.PedidoRequestDTO;
import com.uca.pncparcialfinalrestaurante.dto.PedidoResponseDTO;
import com.uca.pncparcialfinalrestaurante.security.UsuarioPrincipal;
import com.uca.pncparcialfinalrestaurante.services.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO dto,
                                                   @AuthenticationPrincipal UsuarioPrincipal solicitante) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto, solicitante));
    }

    @GetMapping("/mios")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<PedidoResponseDTO>> misPedidos(@AuthenticationPrincipal UsuarioPrincipal solicitante) {
        return ResponseEntity.ok(pedidoService.listarMisPedidos(solicitante));
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN','ENCARGADO')")
    public ResponseEntity<PedidoResponseDTO> confirmar(@PathVariable Long id,
                                                       @AuthenticationPrincipal UsuarioPrincipal solicitante) {
        return ResponseEntity.ok(pedidoService.confirmar(id, solicitante));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','ENCARGADO','CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> cancelar(@PathVariable Long id,
                                                      @AuthenticationPrincipal UsuarioPrincipal solicitante) {
        return ResponseEntity.ok(pedidoService.cancelar(id, solicitante));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN','ENCARGADO')")
    public ResponseEntity<List<PedidoResponseDTO>> porSucursal(@PathVariable Long sucursalId,
                                                               @AuthenticationPrincipal UsuarioPrincipal solicitante) {
        return ResponseEntity.ok(pedidoService.listarPorSucursal(sucursalId, solicitante));
    }
}