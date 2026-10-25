package lima.fernanda.esteticaFernandaLima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.List;

public class UsuarioAtualizacaoDto {

    @Size(min = 3, max = 150)
    @Schema(description = "Nome completo do usuario", example = "Fernanda Lima")
    private String nomeCompleto;

    @Size(min = 11, max = 14)
    @Schema(description = "CPF do usuario", example = "12345678900")
    private String cpf;

    @Size(min = 8, max = 20)
    @Schema(description = "Telefone do usuario", example = "(11) 99999-0000")
    private String telefone;

    @Schema(description = "Descricao/Bio do usuario", example = "Especialista em estetica facial")
    private String bio;

    @Schema(description = "Servicos prestados", example = "[\"Micropigmentacao\", \"Design de Sobrancelhas\"]")
    private List<String> servicosPrestados;

    @Email
    @Size(max = 255)
    @Schema(description = "Email do usuario (unico). Ao alterar o proprio email, a resposta traz um novo token", example = "fernanda@lima.com")
    private String email;

    // ~1,1 MB de imagem em base64; o app envia 400x400 JPEG (bem abaixo disso)
    @Size(max = 1_500_000)
    @Schema(description = "Foto de perfil em data URI (jpeg/png/webp). String vazia remove a foto", example = "data:image/jpeg;base64,/9j/4AAQ...")
    private String fotoPerfil;

    @Schema(description = "Role do usuario (somente leitura, nao pode alterar aqui)", example = "USER")
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

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

