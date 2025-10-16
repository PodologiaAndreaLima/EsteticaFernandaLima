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
              <h3>Fulano</h3>
              <small>15/04/2023</small>
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
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat."
          </p>
        </div>

        <div className="relato">
          <div className="relato-header">
            <div className="avatar"></div>
            <div>
              <h3>Fulano</h3>
              <small>22/05/2023</small>
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
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat."
          </p>
        </div>

        <div className="relato">
          <div className="relato-header">
            <div className="avatar"></div>
            <div>
              <h3>Fulano</h3>
              <small>10/06/2023</small>
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
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat."
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
          Nota <strong>5.0</strong> com base em <strong>49 avaliações</strong>
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
