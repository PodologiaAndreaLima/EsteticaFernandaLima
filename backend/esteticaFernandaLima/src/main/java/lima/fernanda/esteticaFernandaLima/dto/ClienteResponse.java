package lima.fernanda.esteticaFernandaLima.dto;

import java.time.LocalDate;

public class ClienteResponse {
    private Integer id;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;

    // Construtor vazio
    public ClienteResponse() {}

    // Construtor completo
    public ClienteResponse(Integer id, String nomeCompleto, String cpf, String email, String telefone, LocalDate dataNascimento) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
    }

    // Sanitiza dados antes de enviar para o frontend
    public String sanitizarParaFrontend(String texto) {
        if (texto == null) return null;
        return texto
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("&(?![a-zA-Z0-9#]+;)", "&amp;");
    }

    // Getters
    public Integer getId() { return id; }
    public String getNomeCompleto() { 
        return sanitizarParaFrontend(nomeCompleto); 
    }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}