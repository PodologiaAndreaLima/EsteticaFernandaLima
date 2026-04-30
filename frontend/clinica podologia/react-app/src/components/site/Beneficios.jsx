import React from "react";
import { Link } from "react-router-dom";
import "./Beneficios.css";
import AgendarButton from "./AgendarButton";

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
            <h2>Atendimento Personalizado</h2>
            <p>Cada cliente é único.</p>
            <p>
              Nossos procedimentos são indicados de acordo com as necessidades e
              objetivos de cada pessoa, garantindo resultados mais seguros e
              naturais.
            </p>
          </div>

          <div className="card">
            <div className="icon">
              <img
                src="/assets/Signing A Document.png"
                alt="Qualidade, Segurança e Profissionalismo"
              />
            </div>
            <h2>Qualidade, Segurança e Profissionalismo</h2>
            <p>
              Utilizamos produtos de qualidade, técnicas atualizadas e seguimos
              boas práticas para garantir conforto, segurança e bem-estar em
              todos os atendimentos.
            </p>
          </div>
        </div>

        <AgendarButton to="/agendar" text="Agende sua consulta online" />
      </div>
    </section>
  );
};

export default Beneficios;
