package com.uca.pncparcialfinalrestaurante.services.impl;

import com.uca.pncparcialfinalrestaurante.dto.RegistroUsuarioDTO;
import com.uca.pncparcialfinalrestaurante.entity.Sucursal;
import com.uca.pncparcialfinalrestaurante.entity.Usuario;
import com.uca.pncparcialfinalrestaurante.enums.Rol;
import com.uca.pncparcialfinalrestaurante.exception.RecursoNoEncontradoException;
import com.uca.pncparcialfinalrestaurante.repository.SucursalRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.services.UsuarioService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario registrar(RegistroUsuarioDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new EntityExistsException("El username ya existe");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EntityExistsException("El email ya existe");
        }

        Sucursal sucursal = null;
        if (dto.getRol() == Rol.ENCARGADO) {
            if (dto.getSucursalId() == null) {
                throw new IllegalArgumentException("Un ENCARGADO debe tener sucursal asignada");
            }
            sucursal = sucursalRepository.findById(dto.getSucursalId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Sucursal no encontrada"));
        }

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(dto.getRol())
                .sucursal(sucursal)
                .build();

        return usuarioRepository.save(usuario);
    }
}