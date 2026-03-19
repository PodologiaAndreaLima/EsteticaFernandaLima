package lima.fernanda.esteticaFernandaLima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UsuarioCriacaoDto {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúàâãõçÁÉÍÓÚÀÂÃÕÇ\\s'-]+$", message = "Nome contém caracteres inválidos")
    @Schema(description = "Nome completo do funcionário", example = "Fernanda Lima")
    private String nomeCompleto;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "^[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}-?[0-9]{2}$", message = "CPF deve estar no formato: 123.456.789-10 ou 12345678910")
    @Size(min = 11, max = 14)
    @Schema(description = "CPF do funcionário", example = "123.456.789-10")
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\(?[0-9]{2}\\)?\\s?9?[0-9]{4}-?[0-9]{4}$", message = "Telefone inválido. Exemplo: (11) 99999-0000")
    @Size(min = 8, max = 20)
    @Schema(description = "Telefone do funcionário", example = "(11) 99999-0000")
    private String telefone;

    @Size(max = 500, message = "Bio não pode exceder 500 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúàâãõçÁÉÍÓÚÀÂÃÕÇ\\s.,-]*$", message = "Bio contém caracteres inválidos")
    @Schema(description = "Descrição do funcionário", example = "Especialista em estética facial e corporal")
    private String bio;

    @Schema(description = "Serviços prestados", example = "Limpeza de pele, drenagem linfática, massagem relaxante")
    private List<String> servicosPrestados;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Size(max = 100, message = "Email não pode exceder 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email contém caracteres inválidos")
    @Schema(description = "Email do usuário", example = "fernanda@lima.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 20, message = "Senha deve ter entre 8 e 20 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "Senha deve ter no mínimo 8 caracteres, com letra maiúscula, minúscula, número e caractere especial. Exemplo: Estetica@2026"
    )
    @Schema(description = "Senha de acesso", example = "Estetica@2026")
    private String senha;

    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role deve ser ADMIN ou USER")
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

    // Detecta padrões suspeitos de SQL Injection e XSS
    public boolean contemPadraoSuspeito(String entrada) {
        if (entrada == null) return false;

        String pattern = entrada.toLowerCase();

        // Detecta SQL Injection
        if (pattern.matches(".*('|(\\-\\-)|(;)|(\\|\\|)|(\\*)|(\\bor\\b)|(\\band\\b)|(\\bunion\\b)|" +
                "(\\bselect\\b)|(\\binsert\\b)|(\\bupdate\\b)|(\\bdelete\\b)|(\\bdrop\\b)).*")) {
            return true;
        }

        // Detecta XSS
        if (pattern.matches(".*(<script|javascript:|onerror=|onload=|onclick=).*")) {
            return true;
        }

        // Detecta Command Injection
        if (entrada.matches(".*[`$(){}\\[\\]|&;<>].*")) {
            return true;
        }

        return false;
    }
}
