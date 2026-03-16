package lima.fernanda.esteticaFernandaLima.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public class FuncionarioAtualizacaoDto {

    @Size(min = 3, max = 150)
    private String nome;

    @CPF
    private String CPF;

    @Size(min = 8, max = 20)
    private String telefone;

    @Email
    private String email;

    // Opcional: só valida se vier preenchida
    @Size(min = 8, max = 20)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "Senha deve ter no minimo 8 caracteres, com letra maiuscula, minuscula, numero e caractere especial. Exemplo: Estetica@2026"
    )
    private String senha;

    @Size(max = 255)
    private String descricao;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCPF() { return CPF; }
    public void setCPF(String CPF) { this.CPF = CPF; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}

