import React from "react";
import { Link } from "react-router-dom";
import "./SobreNos.css";
import AgendarButton from "./AgendarButton";

const SobreNos = () => {
  return (
    <section className="sobre-nos" id="sobre-nos">
      <div className="sobre-container">
        <div className="sobre-header">
          <h3>Quem Somos</h3>
          <h1>Cuidado e Acolhimento em Cada Detalhe</h1>
          <div className="accent-line"></div>
        </div>

        <div className="sobre-intro">
          <p>
            Bem-vindo à Fernanda Lima Beauty Care, uma clínica de estética
            fundada em 2021 com o propósito de oferecer um atendimento
            verdadeiramente personalizado e acolhedor. Nosso ambiente foi
            cuidadosamente planejado para transmitir harmonia e leveza, onde
            cada cliente é recebido com atenção exclusiva. Mais que
            procedimentos estéticos, oferecemos uma experiência de cuidado
            genuíno, onde o relacionamento humano é nossa maior prioridade.
          </p>
        </div>

        {/* Profissional 1 */}
        <div className="profissional">
          <div className="foto-container">
            <img
              src="/assets/fernanda-lima-foto.jpg"
              alt="Fernanda Lima"
              className="foto"
            />
          </div>
          <div className="texto">
            <h2>Fernanda Lima</h2>
            <span className="cargo">Proprietária & Enfermeira</span>
            <div className="separator"></div>
            <p>
              Atuando na área de estética desde 2016, Fernanda fundou sua
              própria clínica em 2021. Com um olhar humanizado e acolhedor, ela
              se dedica a proporcionar experiências que vão além do resultado
              estético. Seu atendimento personalizado e cuidadoso conquistou a
              confiança de clientes que buscam não apenas beleza, mas também
              bem-estar e um ambiente de acolhimento genuíno.
            </p>
            <div className="especialidades">
              <span>Design de Sobrancelhas</span>
              <span>Micropigmentação</span>
              <span>Limpeza de Pele</span>
              <span>Peeling Facial</span>
              <span>Tratamento Capilar</span>
              <span>Tratamento de Estrias</span>
            </div>
          </div>
        </div>

        {/* Profissional 2 */}
        <div className="profissional reverse">
          <div className="foto-container">
            <img
              src="/assets/Avelino Mouta-foto.jpg"
              alt="Parceiro Profissional"
              className="foto"
            />
          </div>
          <div className="texto">
            <h2>Avelino Mouta</h2>
            <span className="cargo">Micropigmentador</span>
            <div className="separator"></div>
            <p>
              Profissional especializado em atendimentos estéticos
              complementares, com destaque para micropigmentação e depilação
              masculina. Trabalha em parceria com Fernanda, compartilhando os
              mesmos valores de atendimento acolhedor e personalizado. Seu
              trabalho é reconhecido pela técnica apurada e pelo cuidado com
              cada detalhe, garantindo resultados naturais e harmoniosos.
            </p>
            <div className="especialidades">
              <span>Depilação Masculina</span>
              <span>Micropigmentação</span>
              <span>Design de Sobrancelhas</span>
            </div>
          </div>
        </div>

        {/* Botão */}
        <div className="cta">
          <AgendarButton
            to="/agendar"
            text="Agende seu horário em nossa clínica"
          />
        </div>
      </div>
    </section>
  );
};

export default SobreNos;
