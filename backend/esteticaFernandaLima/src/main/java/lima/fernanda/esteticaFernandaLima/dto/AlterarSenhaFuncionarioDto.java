package lima.fernanda.esteticaFernandaLima.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AlterarSenhaFuncionarioDto {

    @NotBlank(message = "Nova senha e obrigatoria")
    @Size(min = 8, max = 100, message = "Nova senha deve ter entre 8 e 100 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
            message = "Senha deve conter ao menos 8 caracteres, incluindo maiuscula, minuscula, numero e simbolo"
    )
    private String novaSenha;

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}

