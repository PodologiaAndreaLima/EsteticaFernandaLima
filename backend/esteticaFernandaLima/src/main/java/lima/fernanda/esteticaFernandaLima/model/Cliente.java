package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Entity
@Table(schema = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 3)
    private String nomeCompleto;

    @CPF
    private String cpf;

    @NotBlank
    private String telefone;

    @NotBlank
    private String email;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    // exclusão lógica: cliente com ordens de serviço não pode ser apagado (FK em ordem_servico)
    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo = true;

    public Integer getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public Boolean getAtivo() { return ativo; }

    public void setId(Integer id) { this.id = id; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setEmail(String email) { this.email = email; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
