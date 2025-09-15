package school.sptech.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "procedimento")
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private Integer tempo;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {

        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {

        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {

        this.descricao = descricao;
    }

    public BigDecimal getValor() {

        return valor;
    }
    public void setValor(BigDecimal valor) {

        this.valor = valor;
    }

    public Integer getTempo() {

        return tempo;
    }
    public void setTempo(Integer tempo) {

        this.tempo = tempo;
    }
}