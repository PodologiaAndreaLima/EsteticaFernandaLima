// Método alternativo para substituição do conteúdo do arquivo

import React, { useRef } from "react";
import "./Relatos.css";
import images from "../../utils/images.js";

const Relatos = () => {
  const relatosRef = useRef(null);

  return (
    <section className="relatos" id="relatos" ref={relatosRef}>
      <div className="relatos-header">
        <h4>Depoimentos</h4>
        <h2>
          O que nossas clientes
          <br />
          dizem sobre nós
        </h2>
        <div className="accent-line"></div>
      </div>

      <div className="relatos-container">
        <div className="relato">
          <div className="relato-header">
            <div className="avatar"></div>
            <div>
              <h3>Barbara Baia</h3>
            </div>
          </div>
          <div className="rating">
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
          </div>
          <p>
            "Fe e incrivel, simpatica atenciosa extremamente caprichosa e
            detalhista. Otima profissional."
          </p>
        </div>

        <div className="relato">
          <div className="relato-header">
            <div className="avatar"></div>
            <div>
              <h3>Julliany Camargo</h3>
            </div>
          </div>
          <div className="rating">
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
          </div>
          <p>
            "Super profissional, ambiente organizado e limpo, amei o design de
            sobrancelha e a limpeza de pele foi muito completa!"
          </p>
        </div>

        <div className="relato">
          <div className="relato-header">
            <div className="avatar"></div>
            <div>
              <h3>Sandra Nakamashi Kawata</h3>
            </div>
          </div>
          <div className="rating">
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
            <img src={images.starFilled} alt="Estrela" />
          </div>
          <p>
            "Sou cliente ha alguns anos e sempre fui muito bem atendida.
            Cuidado, atencao, boa vontade, eficiencia e simpatia. Recomendo."
          </p>
        </div>
      </div>

      <div className="relatos-footer">
        <img
          src="https://upload.wikimedia.org/wikipedia/commons/2/2f/Google_2015_logo.svg"
          alt="Google"
          className="google-logo"
        />
        <p>
          Nota <strong>5.0</strong>
        </p>
        <div className="stars">
          <img src={images.starFilled} alt="Estrela" />
          <img src={images.starFilled} alt="Estrela" />
          <img src={images.starFilled} alt="Estrela" />
          <img src={images.starFilled} alt="Estrela" />
          <img src={images.starFilled} alt="Estrela" />
        </div>
      </div>
    </section>
  );
};

export default Relatos;
