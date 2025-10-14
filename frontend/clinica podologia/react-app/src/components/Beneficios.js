import React from "react";
import { Link } from "react-router-dom";
import "./Beneficios.css";

const Beneficios = () => {
  return (
    <section className="beneficios">
      <div className="beneficios-container">
        <h1>Por que escolher nossa clínica?</h1>

        <div className="cards">
          <div className="card">
            <div className="icon">
              <img src="/assets/Vector.png" alt="Atendimento personalizado" />
            </div>
            <h2>Atendimento personalizado</h2>
            <p>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
              eiusmod tempor incididunt ut labore et dolore magna aliqua.
            </p>
          </div>

          <div className="card">
            <div className="icon">
              <img
                src="/assets/Signing A Document.png"
                alt="Avaliação gratuita"
              />
            </div>
            <h2>Avaliação gratuita</h2>
            <p>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
              eiusmod tempor incididunt ut labore et dolore magna aliqua.
            </p>
          </div>

          <div className="card">
            <div className="icon">
              <img
                src="/assets/mdi_clock-outline.png"
                alt="Acompanhamento contínuo"
              />
            </div>
            <h2>Acompanhamento contínuo</h2>
            <p>
              Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
              eiusmod tempor incididunt ut labore et dolore magna aliqua.
            </p>
          </div>
        </div>

        <Link to="/agendar">
          <button>Agende sua consulta online</button>
        </Link>
      </div>
    </section>
  );
};

export default Beneficios;
