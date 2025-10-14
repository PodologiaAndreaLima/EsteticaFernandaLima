import React, { useRef } from "react";
import "./MVV.css";

const MVV = () => {
  const mvvRef = useRef(null);

  return (
    <section className="mvv" id="mvv" ref={mvvRef}>
      <div className="mvv-header">
        <h4>Missão, Visão e Valores</h4>
        <h2>O que nos move e dá sentido ao nosso propósito</h2>
      </div>

      <div className="mvv-container">
        <div className="mvv-item">
          <div className="icon">
            <img src="/assets/Mission.png" alt="Missão" />
          </div>
          <h3>Missão</h3>
          <p>
            Transformar a autoestima e qualidade de vida de nossos clientes
            através de tratamentos estéticos de excelência, com atendimento
            personalizado e resultados duradouros.
          </p>
        </div>

        <div className="mvv-item">
          <div className="icon">
            <img src="/assets/Eye.png" alt="Visão" />
          </div>
          <h3>Visão</h3>
          <p>
            Ser reconhecida como referência em estética avançada na região,
            inovando constantemente com as melhores técnicas e equipamentos para
            superar as expectativas dos nossos clientes.
          </p>
        </div>

        <div className="mvv-item">
          <div className="icon">
            <img src="/assets/Rose.png" alt="Valores" />
          </div>
          <h3>Valores</h3>
          <p>
            Ética profissional, compromisso com resultados, excelência no
            atendimento, inovação constante e respeito à individualidade de cada
            cliente em todo o processo.
          </p>
        </div>
      </div>
    </section>
  );
};

export default MVV;
