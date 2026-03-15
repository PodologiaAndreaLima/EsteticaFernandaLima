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

    private static final int MAX_TENTATIVAS_LOGIN = 5;
    private static final long BLOQUEIO_MINUTOS = 5;
    private static final long BLOQUEIO_MS = BLOQUEIO_MINUTOS * 60 * 1000;

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

    public void registrarTentativaFalha(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            if (usuario.isBloqueado()) {
                return;
            }

            int tentativas = usuario.getTentativasLogin() + 1;
            usuario.setTentativasLogin(tentativas);
            if (tentativas >= MAX_TENTATIVAS_LOGIN) {
                long bloqueadoAte = System.currentTimeMillis() + BLOQUEIO_MS;
                usuario.setBloqueadoAte(bloqueadoAte);
                System.out.println("Usuário bloqueado por 5 minutos: " + email);
            }
            usuarioRepository.save(usuario);
        });
    }

    public void resetarTentativasLogin(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            if (usuario.getTentativasLogin() != 0) {
                usuario.setTentativasLogin(0);
                usuario.setBloqueadoAte(null);
                usuarioRepository.save(usuario);
            }
        });
    }
}
