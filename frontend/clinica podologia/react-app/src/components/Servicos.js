import React from "react";
import "./Servicos.css";

const Servicos = () => {
  return (
    <section className="servicos" id="servicos">
      <div className="servicos-header">
        <h3>Nossos Serviços</h3>
        <h1>Cuidados Personalizados para Realçar Sua Beleza Natural</h1>
      </div>

      {/* Serviço 1 */}
      <div className="servico">
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
        <div className="texto">
          <h2>Design e Micropigmentação de Sobrancelhas</h2>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
            pariatur.
          </p>
        </div>
      </div>

      {/* Serviço 2 (texto primeiro, circle depois) */}
      <div className="servico texto-first">
        <div className="texto">
          <h2>Limpeza de Pele e Peeling Facial</h2>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
            pariatur.
          </p>
        </div>
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
      </div>

      {/* Serviço 3 */}
      <div className="servico">
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
        <div className="texto">
          <h2>Tratamento de Estrias e Tratamento Capilar</h2>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
            pariatur.
          </p>
        </div>
      </div>

      {/* Serviço 4 */}
      <div className="servico texto-first">
        <div className="texto">
          <h2>Depilação Completa</h2>
          <p>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
            pariatur.
          </p>
        </div>
        <div className="circle">
          {/* Aqui pode ser inserida uma imagem relacionada ao serviço */}
        </div>
      </div>
    </section>
  );
};

export default Servicos;
