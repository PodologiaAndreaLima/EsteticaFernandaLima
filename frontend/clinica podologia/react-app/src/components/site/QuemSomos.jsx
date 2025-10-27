import React from "react";
import "./QuemSomos.css";

const QuemSomos = () => {
  return (
    <section className="quem-somos" id="quem-somos">
      <div className="quem-somos-texto">
        <h3>QUEM SOMOS</h3>
        <h1>Você já conhece nossa clínica?</h1>
        <p>
          Na Clínica Fernanda Lima Beauty Care, acreditamos que cuidar de si é
          um ato de amor. Desde 2021, oferecemos tratamentos estéticos que unem
          técnica, bem-estar e acolhimento, para que cada cliente se sinta
          confiante, especial e única.
        </p>

        <p>
          Nosso espaço foi criado para proporcionar momentos de leveza e
          autoestima, com atendimento humanizado e profissionais apaixonados
          pelo que fazem. Aqui, cada detalhe é pensado para que você saia se
          sentindo ainda melhor do que chegou.
        </p>
      </div>
      <div className="quem-somos-img"></div>
    </section>
  );
};

export default QuemSomos;
