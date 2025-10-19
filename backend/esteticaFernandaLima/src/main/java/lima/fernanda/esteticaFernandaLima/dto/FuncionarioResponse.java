package lima.fernanda.esteticaFernandaLima.dto;

public record FuncionarioResponse(
        Integer idFuncionario,
        String nome,
        String CPF,
        String telefone,
        String email,
        String descricao,
        String senha
) {
    public static FuncionarioResponse fromFuncionario(lima.fernanda.esteticaFernandaLima.model.Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getIdFuncionario(),
                funcionario.getNome(),
                funcionario.getCPF(),
                funcionario.getTelefone(),
                funcionario.getEmail(),
                funcionario.getDescricao(),
                funcionario.getSenha()
        );
    }
}
