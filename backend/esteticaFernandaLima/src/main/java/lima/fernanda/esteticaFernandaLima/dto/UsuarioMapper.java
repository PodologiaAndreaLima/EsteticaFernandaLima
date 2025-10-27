package lima.fernanda.esteticaFernandaLima.dto;

import lima.fernanda.esteticaFernandaLima.model.Usuario;

public class UsuarioMapper {

    public static Usuario of(UsuarioCriacaoDto usuarioCriacaoDto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioCriacaoDto.getEmail());
        usuario.setNome(usuarioCriacaoDto.getNome());
        usuario.setSenha(usuarioCriacaoDto.getSenha());

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
        usuarioTokenDto.setNome(usuario.getNome());
        usuarioTokenDto.setEmail(usuario.getEmail());
        usuarioTokenDto.setToken(token);

        return usuarioTokenDto;
    }

    public static UsuarioListarDto of(Usuario usuario) {
        UsuarioListarDto usuarioListarDto = new UsuarioListarDto();
        usuarioListarDto.setId(usuario.getId());
        usuarioListarDto.setNome(usuario.getNome());
        usuarioListarDto.setEmail(usuario.getEmail());

        return usuarioListarDto;
    }
}
