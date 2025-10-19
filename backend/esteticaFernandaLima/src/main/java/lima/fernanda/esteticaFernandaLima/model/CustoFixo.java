package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class CustoFixo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCustoFixo;
    private String nome;
    private String descricao;
    private Float valorMensal;

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
}
