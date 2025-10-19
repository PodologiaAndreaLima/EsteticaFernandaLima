package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ComboServicoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComboServicoProduto;

    public Integer getIdComboServicoProduto() {
        return idComboServicoProduto;
    }

    public void setIdComboServicoProduto(Integer idComboServicoProduto) {
        this.idComboServicoProduto = idComboServicoProduto;
    }
}
