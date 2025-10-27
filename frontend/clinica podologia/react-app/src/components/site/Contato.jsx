import React from "react";
import { Link } from "react-router-dom";
import "./Contato.css";
import AgendarButton from "./AgendarButton";
import images from "../../utils/images.js";

const Contato = () => {
  return (
    <section className="contato" id="contato">
      <div className="contato-header">
        <h4>Contato e Localização</h4>
        <h2>Como e onde nos encontrar?</h2>
      </div>
      <div className="contato-container">
        {/* Mapa */}
        <div className="mapa">
          <iframe
            src="https://www.google.com/maps/embed?pb=!1m23!1m12!1m3!1d111309.96513761318!2d-46.68027859028895!3d-23.478287947444514!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!4m8!3e6!4m0!4m5!1s0x94cef7133029a23b%3A0x543562e9417e269!2sAv.%20Guapira%2C%20600%20-%20Tucuruvi%2C%20S%C3%A3o%20Paulo%20-%20SP%2C%2002265-001!3m2!1d-23.4783096!2d-46.597877!5e1!3m2!1spt-BR!2sbr!4v1759833165510!5m2!1spt-BR!2sbr"
            width="100%"
            height="450"
            style={{ border: 0, borderRadius: "8px" }}
            allowFullScreen=""
            loading="lazy"
            referrerPolicy="no-referrer-when-downgrade"
            title="Localização da Clínica"
          ></iframe>
        </div>

        {/* Informações */}
        <div className="contato-info">
          <div className="info-left">
            <p>
              <img src={images.downtown} alt="Localização" />
              Av. Guapira, 600 - Tucuruvi, São Paulo - SP, 02265-001
            </p>
            <p>
              <img src={images.clock} alt="Horário" />
              Segunda à Sábado: 09h - 21h <br /> Domingos: 09h - 16h
            </p>
          </div>
          <div className="info-right">
            <p>
              <img src={images.whatsapp} alt="WhatsApp" />
              (11) 97249-2829
            </p>
            <p>
              <img src={images.instagram} alt="Instagram" />
              fernandalima_beauty
            </p>
            <p>
              <img src={images.facebook} alt="Facebook" />
              Fernanda Lima Beauty Care
            </p>
          </div>
        </div>
      </div>
      <div className="btn-contato">
        <AgendarButton to="/agendar" text="Agende sua consulta online" />
      </div>
    </section>
  );
};

export default Contato;
