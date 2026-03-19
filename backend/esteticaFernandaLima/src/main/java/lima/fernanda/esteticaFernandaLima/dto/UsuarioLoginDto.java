package lima.fernanda.esteticaFernandaLima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioLoginDto {

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    @Size(max = 100, message = "Email não pode exceder 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email contém caracteres inválidos")
    @Schema(description = "Email do cliente", example = "fernanda@lima.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 20, message = "Senha deve ter entre 8 e 20 caracteres")
    @Schema(description = "Senha do cliente", example = "Estetica@2026")
    private String senha;

    // Valida padrões suspeitos no login
    public boolean temPadraoSuspeito() {
        if (email != null && email.toLowerCase().matches(".*('|(\\-\\-)|(;)|(\\|\\|)|(UNION)|(SELECT)).*")) {
            return true;
        }
        return false;
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
}


