package lima.fernanda.esteticaFernandaLima.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UsuarioLoginDto {

    @Schema(description = "Email do cliente", example = "fernanda@lima.com")
    private String email;

    @Schema(description = "Senha do cliente", example = "senha123")
    private String senha;

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
