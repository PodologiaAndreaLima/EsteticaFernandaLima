package lima.fernanda.esteticaFernandaLima.dto;

public class ClienteResponse {
    private Integer id;
    private String nomeCompleto;
    private String email;

    public ClienteResponse(Integer id, String nomeCompleto, String email) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
    }

    // getters e setters
    public Integer getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail() { return email; }
}
