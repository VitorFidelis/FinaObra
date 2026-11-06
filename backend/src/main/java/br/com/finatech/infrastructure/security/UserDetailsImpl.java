package br.com.finatech.infrastructure.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class UserDetailsImpl implements UserDetails {

    private UUID id;
    private String username;

    // Adicione ou garanta este construtor
    public UserDetailsImpl(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return this.id;
    }
    
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }
}
