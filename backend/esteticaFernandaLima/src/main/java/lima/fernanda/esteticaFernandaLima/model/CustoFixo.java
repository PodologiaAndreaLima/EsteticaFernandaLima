package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class CustoFixo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCustoFixo;
    private String nome;
    private String descricao;
    private Float valorMensal;
    private LocalDate data;

    public Integer getIdCustoFixo() {
        return idCustoFixo;
    }

    public void setIdCustoFixo(Integer idCustoFixo) {
        this.idCustoFixo = idCustoFixo;
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

    public Float getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(Float valorMensal) {
        this.valorMensal = valorMensal;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
