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

    public Integer getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public LocalDate getDataNascimento() { return dataNascimento; }

    public void setId(Integer id) { this.id = id; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setEmail(String email) { this.email = email; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}
