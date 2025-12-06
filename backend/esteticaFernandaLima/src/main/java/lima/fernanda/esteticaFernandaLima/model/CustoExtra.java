package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(schema = "custo_extra")
public class CustoExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCustoExtra;
    private String nome;
    private String descricao;
    private Float valor;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;

    public Integer getIdCustoExtra() {
        return idCustoExtra;
    }

    public void setIdCustoExtra(Integer idCustoExtra) {
        this.idCustoExtra = idCustoExtra;
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

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
