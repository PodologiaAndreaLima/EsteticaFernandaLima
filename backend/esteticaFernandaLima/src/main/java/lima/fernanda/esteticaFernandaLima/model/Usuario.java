package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;
import lima.fernanda.esteticaFernandaLima.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    private String cpf;
    private String telefone;
    private String bio;

    @ElementCollection
    private List<String> servicosPrestados;

    @Column(unique = true)
    private String email;

    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<OrdemServico> ordensServico = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ServicoProduto> servicosProdutos;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int tentativasLogin = 0;

    @Column(name = "bloqueado_ate")
    private Long bloqueadoAte = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<OrdemServico> getOrdensServico() {
        return ordensServico;
    }

    public void setOrdensServico(List<OrdemServico> ordensServico) {
        this.ordensServico = ordensServico;
    }

    public void setServicosProdutos(List<ServicoProduto> servicosProdutos) {
        this.servicosProdutos = servicosProdutos;
    }

    public int getTentativasLogin() {
        return tentativasLogin;
    }

    public void setTentativasLogin(int tentativasLogin) {
        this.tentativasLogin = tentativasLogin;
    }

    public Long getBloqueadoAte() {
        return bloqueadoAte;
    }

    public void setBloqueadoAte(Long bloqueadoAte) {
        this.bloqueadoAte = bloqueadoAte;
    }

    public boolean isBloqueado() {
        if (bloqueadoAte == null) {
            return false;
        }
        long agora = System.currentTimeMillis();
        if (agora > bloqueadoAte) {
            // Bloqueio expirou, reseta
            this.bloqueadoAte = null;
            this.tentativasLogin = 0;
            return false;
        }
        return true;
    }
}
