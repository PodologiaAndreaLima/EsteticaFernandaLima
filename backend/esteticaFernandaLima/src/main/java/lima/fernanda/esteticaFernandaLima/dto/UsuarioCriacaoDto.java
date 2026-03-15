package lima.fernanda.esteticaFernandaLima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UsuarioCriacaoDto {

    @NotBlank
    @Size(min = 3, max = 150)
    @Schema(description = "Nome completo do funcionário", example = "Fernanda Lima")
    private String nomeCompleto;

    @NotBlank
    @Size(min = 11, max = 14)
    @Schema(description = "CPF do funcionário", example = "12345678900")
    private String cpf;

    @NotBlank
    @Size(min = 8, max = 20)
    @Schema(description = "Telefone do funcionário", example = "(11) 99999-0000")
    private String telefone;

    @Schema(description = "Descrição do funcionário", example = "Especialista em estética facial e corporal")
    private String bio;

    @Schema(description = "Serviços prestados", example = "Limpeza de pele, drenagem linfática, massagem relaxante")
    private List<String> servicosPrestados;

    @Email
    @NotBlank
    @Schema(description = "Email do usuário", example = "fernanda@lima.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "Senha deve ter no minimo 8 caracteres, com letra maiuscula, minuscula, numero e caractere especial. Exemplo: Estetica@2026"
    )
    @Schema(description = "Senha de acesso", example = "Estetica@2026")
    private String senha;

    @Schema(description = "Tipo de acesso: ADMIN ou USER")
    private String role;

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
