import React, { useState, useEffect, useRef } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faMapMarkerAlt,
  faChevronLeft,
  faChevronRight,
  faCertificate,
} from "@fortawesome/free-solid-svg-icons";
import { faCalendarAlt } from "@fortawesome/free-regular-svg-icons";
import "./EventosCarrossel.css";

// Dados dos eventos e palestras - serão substituídos pelas imagens reais
const eventosData = [
  {
    imagem:
      "https://via.placeholder.com/800x500?text=Congresso+Estetica+Avancada",
    titulo: "Congresso Nacional de Estética Avançada 2024",
    descricao:
      "Participação como palestrante no maior evento de Estética do Brasil, abordando técnicas inovadoras em tratamentos faciais e corporais.",
    data: "Outubro 2024",
    local: "São Paulo - SP",
  },
  {
    imagem: "https://via.placeholder.com/800x500?text=Simposio+Estetica+Facial",
    titulo: "Simpósio de Estética Facial",
    descricao:
      "Palestra educativa para profissionais da área sobre as mais recentes tecnologias em rejuvenescimento e cuidados com a pele.",
    data: "Julho 2024",
    local: "Rio de Janeiro - RJ",
  },
  {
    imagem:
      "https://via.placeholder.com/800x500?text=Workshop+Tecnicas+Esteticas",
    titulo: "Workshop de Técnicas Estéticas Avançadas",
    descricao:
      "Ministração de workshop prático sobre novas tecnologias aplicadas aos tratamentos corporais redutores e modeladores.",
    data: "Março 2024",
    local: "Belo Horizonte - MG",
  },
  {
    imagem: "https://via.placeholder.com/800x500?text=Congresso+Internacional",
    titulo: "Congresso Internacional de Estética",
    descricao:
      "Representação brasileira no maior congresso internacional da área, compartilhando estudos de caso e protocolos inovadores.",
    data: "Maio 2023",
    local: "Barcelona - Espanha",
  },
  {
    imagem: "https://via.placeholder.com/800x500?text=Jornada+Estetica",
    titulo: "III Jornada de Estética e Bem-estar",
    descricao:
      "Organização e participação como debatedora na jornada que reuniu os principais profissionais do setor para discutir tendências e inovações.",
    data: "Setembro 2023",
    local: "Brasília - DF",
  },
];

const EventosCarrossel = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isTransitioning, setIsTransitioning] = useState(false);

  // Avança para o próximo slide
  const nextSlide = () => {
    if (!isTransitioning) {
      setIsTransitioning(true);
      setCurrentIndex((prevIndex) =>
        prevIndex === eventosData.length - 1 ? 0 : prevIndex + 1
      );
      setTimeout(() => setIsTransitioning(false), 500); // Tempo da transição
    }
  };

  // Retorna ao slide anterior
  const prevSlide = () => {
    if (!isTransitioning) {
      setIsTransitioning(true);
      setCurrentIndex((prevIndex) =>
        prevIndex === 0 ? eventosData.length - 1 : prevIndex - 1
      );
      setTimeout(() => setIsTransitioning(false), 500); // Tempo da transição
    }
  };

  // Avança automaticamente os slides a cada 5 segundos
  useEffect(() => {
    const interval = setInterval(() => {
      nextSlide();
    }, 5000);

    return () => clearInterval(interval);
  }, [currentIndex]);

  const eventosRef = useRef(null);

  return (
    <section id="eventos" className="eventos" ref={eventosRef}>
      <div className="eventos-container">
        <div className="eventos-heading-wrapper">
          <div>
            <h3>EXPERIÊNCIA E CONHECIMENTO</h3>
            <h1>Eventos e Palestras</h1>
          </div>
        </div>
        <p className="eventos-descricao">
          Conheça os principais eventos e palestras dos quais nossa equipe
          participou, compartilhando conhecimentos e experiências para o avanço
          da estética e do bem-estar no Brasil e no mundo.
        </p>

        <div className="carrossel-container">
          <button
            className="carrossel-btn prev"
            onClick={prevSlide}
            aria-label="Evento anterior"
          >
            <FontAwesomeIcon icon={faChevronLeft} />
          </button>

          <div className="carrossel-view">
            <div
              className={`carrossel-track ${
                isTransitioning ? "transitioning" : ""
              }`}
              style={{ transform: `translateX(-${currentIndex * 100}%)` }}
            >
              {eventosData.map((evento, index) => (
                <div className="carrossel-slide" key={index}>
                  <div className="evento-card">
                    <div className="evento-image">
                      <img
                        src={evento.imagem}
                        alt={`Evento: ${evento.titulo}`}
                        onError={(e) => {
                          e.target.onerror = null;
                          e.target.src =
                            "https://via.placeholder.com/400x300?text=Evento+Estetica";
                        }}
                      />
                    </div>
                    <div className="evento-info">
                      <h3>{evento.titulo}</h3>
                      <p className="evento-descricao">{evento.descricao}</p>
                      <div className="evento-metadata">
                        <span className="evento-data">
                          <FontAwesomeIcon icon={faCalendarAlt} /> {evento.data}
                        </span>
                        <span className="evento-local">
                          <FontAwesomeIcon icon={faMapMarkerAlt} />{" "}
                          {evento.local}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <button
            className="carrossel-btn next"
            onClick={nextSlide}
            aria-label="Próximo evento"
          >
            <FontAwesomeIcon icon={faChevronRight} />
          </button>
        </div>
      </div>
    </section>
  );
};

export default EventosCarrossel;
