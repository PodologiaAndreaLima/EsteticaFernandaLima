package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;

@Entity
@Table(schema = "combo_servico_produto")
public class ComboServicoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComboServicoProduto;

    @ManyToOne
    @JoinColumn(name = "fk_servico_produto")
    private ServicoProduto servicoProduto;

    @ManyToOne
    @JoinColumn(name = "fk_combo")
    private Combo combo;

    public Integer getIdComboServicoProduto() {
        return idComboServicoProduto;
    }

    public void setIdComboServicoProduto(Integer idComboServicoProduto) {
        this.idComboServicoProduto = idComboServicoProduto;
    }

    public ServicoProduto getServicoProduto() {
        return servicoProduto;
    }

    public void setServicoProduto(ServicoProduto servicoProduto) {
        this.servicoProduto = servicoProduto;
    }

    public Combo getCombo() {
        return combo;
    }

    public void setCombo(Combo combo) {
        this.combo = combo;
    }
}
