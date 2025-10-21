import React, { useState, useEffect, useRef } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";
import "./EventosCarrossel.css";

const eventosData = [
  {
    imagem: "/assets/evento-foto1.jpg",
  },
  {
    imagem: "/assets/evento-foto2.jpg",
  },
  {
    imagem: "/assets/evento-foto3.jpg",
  },
  {
    imagem: "/assets/evento-foto4.jpg",
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
          <h3 className="eventos-subtitulo">EXPERIÊNCIA E CONHECIMENTO</h3>
          <h1 className="eventos-titulo">Eventos & Workshops</h1>
        </div>
        <div className="carrossel-container">
          <button
            className="carrossel-btn prev"
            onClick={prevSlide}
            aria-label="Evento anterior"
          >
            <FontAwesomeIcon icon={faChevronLeft} />
          </button>
          <div className="carrossel-view">
            <div className="evento-card">
              <img
                src={eventosData[currentIndex].imagem}
                alt={`Evento ${currentIndex + 1}`}
                className="evento-img"
              />
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
