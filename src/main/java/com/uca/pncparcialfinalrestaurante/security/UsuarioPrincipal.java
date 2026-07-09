package com.uca.pncparcialfinalrestaurante.security;

import com.uca.pncparcialfinalrestaurante.entity.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String rol;
    private final Long sucursalId;

    public UsuarioPrincipal(Usuario usuario) {
        this.userId = usuario.getId();
        this.username = usuario.getUsername();
        this.password = usuario.getPassword();
        this.rol = usuario.getRol().name();
        this.sucursalId = usuario.getSucursal() != null ? usuario.getSucursal().getId() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}