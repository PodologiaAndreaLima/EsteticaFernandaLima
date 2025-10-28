import React from "react";
import "./Footer.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-top">
          <div className="footer-logo">
            <img
              src="/assets/footerLogo-removebg-preview.png"
              alt="Logo Fernanda Lima"
              className="footer-logo-img"
            />
          </div>

          <div className="footer-links">
            <div className="footer-column">
              <h3>Menu</h3>
              <ul>
                <li>
                  <a href="#inicio">Início</a>
                </li>
                <li>
                  <a href="#servicos">Serviços</a>
                </li>
                <li>
                  <a href="#sobre-nos">Sobre</a>
                </li>
                <li>
                  <a href="#beneficios">Benefícios</a>
                </li>
                <li>
                  <a href="#relatos">Depoimentos</a>
                </li>
                <li>
                  <a href="#contato">Contato</a>
                </li>
              </ul>
            </div>

            <div className="footer-column">
              <h3>Atendimento</h3>
              <ul>
                <li>Terça a Sexta</li>
                <li>8:00 - 19:00</li>
                <li>Sábado</li>
                <li>8:00 - 15:00</li>
                <li>Domingo</li>
                <li>Fechado</li>
              </ul>
            </div>

            <div className="footer-column">
              <h3>Contato</h3>
              <ul>
                <li>Tel: (11) 98014-1736</li>
                <li>Av. Guapira, 600</li>
                <li>Tucuruvi, São Paulo - SP</li>
                <li>CEP: 02265-001</li>
              </ul>
            </div>
          </div>
        </div>

        {/* Ícones sociais */}
        <div className="footer-icons">
          <a
            href="https://wa.me/5511972492829"
            target="_blank"
            rel="noopener noreferrer"
          >
            <img src="/assets/WhatsApp (3).png" alt="WhatsApp" />
          </a>
          <a
            href="https://facebook.com/fernandalima.beautycare"
            target="_blank"
            rel="noopener noreferrer"
          >
            <img src="/assets/Facebook (1).png" alt="Facebook" />
          </a>
          <a
            href="https://instagram.com/fernandalima.beautycare"
            target="_blank"
            rel="noopener noreferrer"
          >
            <img src="/assets/Instagram (1).png" alt="Instagram" />
          </a>
        </div>

        {/* Linha horizontal */}
        <hr className="footer-line" />

        {/* Texto abaixo da linha */}
        <p className="footer-text">
          ©{new Date().getFullYear()} Fernanda Lima Beauty Care - Todos os
          direitos reservados
        </p>
        <p className="footer-developer">
          Desenvolvido por{" "}
          <a href="mailto:contato@developer.com">Your Agency</a>
        </p>
      </div>
    </footer>
  );
};

export default Footer;
