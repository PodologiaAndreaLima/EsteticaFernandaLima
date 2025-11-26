package lima.fernanda.esteticaFernandaLima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class UsuarioListarDto {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "Fernanda Lima")
    private String nome;

    @Schema(description = "Nome completo do usuário", example = "Fernanda Lima da Silva")
    private String nomeCompleto;

    @Schema(description = "Email do usuário", example = "fernanda@lima.com")
    private String email;

    @Schema(description = "CPF do usuário", example = "123.456.789-00")
    private String cpf;

    @Schema(description = "Telefone do usuário", example = "(11) 99999-0000")
    private String telefone;

    @Schema(description = "Descrição do usuário", example = "Especialista em estética")
    private String bio;

    @Schema(description = "Serviços prestados", example = "Limpeza de pele, Micropigmentação")
    private List<String> servicosPrestados;

    @Schema(description = "Cargo/Role do usuário", example = "ADMIN")
    private String role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getServicosPrestados() {
        return servicosPrestados;
    }

    public void setServicosPrestados(List<String> servicosPrestados) {
        this.servicosPrestados = servicosPrestados;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}