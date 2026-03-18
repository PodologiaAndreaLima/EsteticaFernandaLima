package lima.fernanda.esteticaFernandaLima.dto;

import lima.fernanda.esteticaFernandaLima.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

import lima.fernanda.esteticaFernandaLima.model.Usuario;

public class UsuarioDetalhesDto implements UserDetails {

    private final Long id;

    private final String nome;

    private final String email;

    private final String senha;

    private final Usuario usuario;

    public UsuarioDetalhesDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNomeCompleto();
        this.email = usuario.getEmail();
        this.senha = usuario.getSenha();
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getNome(){
        return nome;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role userRole = usuario != null ? usuario.getRole() : null;

        // Se por algum motivo o role não estiver setado, fallback para USER
        if (userRole == null) {
            userRole = Role.USER;
        }

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + userRole.name())
        );
    }
    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !usuario.isBloqueado();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
