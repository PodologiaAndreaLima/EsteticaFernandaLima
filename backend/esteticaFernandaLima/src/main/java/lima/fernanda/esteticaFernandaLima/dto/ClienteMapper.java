package lima.fernanda.esteticaFernandaLima.dto;

import lima.fernanda.esteticaFernandaLima.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClienteMapper {

    public ClienteResponse toResponse(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        return new ClienteResponse(
                cliente.getId(),
                cliente.getNomeCompleto(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getDataNascimento()
        );
    }

    public List<ClienteResponse> toResponseList(List<Cliente> clientes) {
        return clientes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}