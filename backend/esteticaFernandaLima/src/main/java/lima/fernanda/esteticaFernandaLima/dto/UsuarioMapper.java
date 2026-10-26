package lima.fernanda.esteticaFernandaLima.dto;

import lima.fernanda.esteticaFernandaLima.enums.Role;
import lima.fernanda.esteticaFernandaLima.model.Usuario;

public class UsuarioMapper {

    public static Usuario of(UsuarioCriacaoDto usuarioCriacaoDto) {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(usuarioCriacaoDto.getNomeCompleto());
        usuario.setCpf(usuarioCriacaoDto.getCpf());
        usuario.setTelefone(usuarioCriacaoDto.getTelefone());
        usuario.setBio(usuarioCriacaoDto.getBio());
        usuario.setServicosPrestados(usuarioCriacaoDto.getServicosPrestados());
        usuario.setEmail(usuarioCriacaoDto.getEmail());
        usuario.setSenha(usuarioCriacaoDto.getSenha());
        
        String roleStr = usuarioCriacaoDto.getRole();
        if (roleStr == null || roleStr.isEmpty()) {
            usuario.setRole(Role.USER);
        } else {
            try {
                usuario.setRole(Role.valueOf(roleStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                usuario.setRole(Role.USER);
            }
        }

        return usuario;
    }

    public static Usuario of(UsuarioLoginDto clienteLoginDto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(clienteLoginDto.getEmail());
        usuario.setSenha(clienteLoginDto.getSenha());
        return usuario;
    }

    public static UsuarioTokenDto of(Usuario usuario, String token) {
        UsuarioTokenDto usuarioTokenDto = new UsuarioTokenDto();
        usuarioTokenDto.setUserId(usuario.getId());
        usuarioTokenDto.setNome(usuario.getNomeCompleto());
        usuarioTokenDto.setEmail(usuario.getEmail());
        usuarioTokenDto.setToken(token);
        
        if (usuario.getRole() != null) {
            usuarioTokenDto.setRole(usuario.getRole().name());
        } else {
            usuarioTokenDto.setRole("USER");
        }

        return usuarioTokenDto;
    }

    public static UsuarioListarDto of(Usuario usuario) {
    UsuarioListarDto usuarioListarDto = new UsuarioListarDto();
    usuarioListarDto.setId(usuario.getId());
    usuarioListarDto.setNome(usuario.getNomeCompleto());
    usuarioListarDto.setNomeCompleto(usuario.getNomeCompleto());
    usuarioListarDto.setEmail(usuario.getEmail());
    usuarioListarDto.setCpf(usuario.getCpf());
    usuarioListarDto.setTelefone(usuario.getTelefone());
    usuarioListarDto.setBio(usuario.getBio());
    usuarioListarDto.setServicosPrestados(usuario.getServicosPrestados());
    
    if (usuario.getRole() != null) {
        usuarioListarDto.setRole(usuario.getRole().name());
    } else {
        usuarioListarDto.setRole("USER");
    }
    
    return usuarioListarDto;
}
}
