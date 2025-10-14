package lima.fernanda.esteticaFernandaLima.model;

import jakarta.persistence.*;

@Entity
@Table
public class ServicoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



}
