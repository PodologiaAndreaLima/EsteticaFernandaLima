package lima.fernanda.esteticaFernandaLima.service;

import lima.fernanda.esteticaFernandaLima.dto.UsuarioDetalhesDto;
import lima.fernanda.esteticaFernandaLima.model.Usuario;
import lima.fernanda.esteticaFernandaLima.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    //Metodo da 'interface' implementada
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("Tentando autenticar usuário: " + username);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(username);

        if(usuarioOpt.isEmpty()) {
            System.out.println("Usuário não encontrado no banco: " + username);
            throw new UsernameNotFoundException(String.format("usuario: %s não encontrado", username));
        }

        System.out.println("Usuário encontrado: " + usuarioOpt.get().getEmail());
        return new UsuarioDetalhesDto(usuarioOpt.get());
    }
}
