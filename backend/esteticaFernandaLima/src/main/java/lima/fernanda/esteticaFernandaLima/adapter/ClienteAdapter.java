package lima.fernanda.esteticaFernandaLima.adapter;

import org.springframework.stereotype.Component;
import lima.fernanda.esteticaFernandaLima.model.Cliente;
import lima.fernanda.esteticaFernandaLima.dto.ClienteResponse;

@Component
public class ClienteAdapter implements Adapter<Cliente, ClienteResponse> {

    @Override
    public ClienteResponse adapt(Cliente cliente) {
        if (cliente == null) return null;
        
        return new ClienteResponse(
            cliente.getId(),
            cliente.getNomeCompleto(),
            cliente.getCpf(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getDataNascimento()
        );
    }
}