package lima.fernanda.esteticaFernandaLima.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

// resposta do PUT /usuarios/{id}: os dados atualizados + um novo token quando o
// proprio usuario logado troca o e-mail (o "subject" do JWT e o e-mail)
public class UsuarioAtualizadoDto extends UsuarioListarDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Novo JWT, presente apenas quando o usuario logado alterou o proprio e-mail")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
