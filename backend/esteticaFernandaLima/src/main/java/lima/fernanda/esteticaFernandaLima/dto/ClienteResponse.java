package lima.fernanda.esteticaFernandaLima.dto;

import lima.fernanda.esteticaFernandaLima.model.Cliente;


public record ClienteResponse(
        Integer id,
        String nomeCompleto,
        String cpf,
        String telefone,
        String email
) {
    public static ClienteResponse fromCliente(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNomeCompleto(),
                cliente.getCpf(),
                cliente.getTelefone(),
                cliente.getEmail()
        );
    }
}
