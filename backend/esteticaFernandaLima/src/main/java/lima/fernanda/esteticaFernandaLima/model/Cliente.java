// java
package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

@Entity
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

    @NotBlank
    private String senha;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrdemServico> ordensServico = new ArrayList<>();

    // getters e setters (incluindo ordensServico)
    public Integer getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getCpf() { return cpf; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public List<OrdemServico> getOrdensServico() { return ordensServico; }

    public void setId(Integer id) { this.id = id; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setOrdensServico(List<OrdemServico> ordensServico) { this.ordensServico = ordensServico; }
}
