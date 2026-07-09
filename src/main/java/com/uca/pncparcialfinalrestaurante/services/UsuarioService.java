package com.uca.pncparcialfinalrestaurante.services;


import com.uca.pncparcialfinalrestaurante.dto.RegistroUsuarioDTO;
import com.uca.pncparcialfinalrestaurante.entity.Usuario;

public interface UsuarioService {
    Usuario registrar(RegistroUsuarioDTO dto);
}