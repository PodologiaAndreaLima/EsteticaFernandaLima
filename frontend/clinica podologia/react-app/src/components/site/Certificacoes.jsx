import React, { useState, useEffect, useRef } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faGraduationCap,
  faChevronLeft,
  faChevronRight,
  faCertificate,
} from "@fortawesome/free-solid-svg-icons";
import "./Certificacoes.css";
import "../../styles/fix-scrollbars.css";

// Dados de certificações - substitua pelo conteúdo real quando disponível
const certificacoesData = [
  {
    id: 1,
    titulo: "Especialização em Estética Avançada",
    instituicao: "Universidade de São Paulo",
    ano: "2022",
    imagem:
      "https://via.placeholder.com/400x300?text=Certificado+Estetica+Avancada",
  },
  {
    id: 2,
    titulo: "Técnicas Avançadas em Tratamentos Faciais",
    instituicao: "Instituto Brasileiro de Estética",
    ano: "2021",
    imagem:
      "https://via.placeholder.com/400x300?text=Certificado+Tecnicas+Faciais",
  },
  {
    id: 3,
    titulo: "Pós-Graduação em Cosmetologia",
    instituicao: "Faculdade de Ciências da Saúde",
    ano: "2020",
    imagem: "https://via.placeholder.com/400x300?text=Certificado+Cosmetologia",
  },
  {
    id: 4,
    titulo: "Especialização em Massoterapia",
    instituicao: "Instituto de Terapias Integrativas",
    ano: "2019",
    imagem: "https://via.placeholder.com/400x300?text=Certificado+Massoterapia",
  },
  {
    id: 5,
    titulo: "Curso Avançado em Estética Corporal",
    instituicao: "Centro de Estudos em Estética",
    ano: "2018",
    imagem:
      "https://via.placeholder.com/400x300?text=Certificado+Estetica+Corporal",
  },
];

const Certificacoes = () => {
  const [currentSlide, setCurrentSlide] = useState(0);
  const [touchStart, setTouchStart] = useState(0);
  const [touchEnd, setTouchEnd] = useState(0);
  const [isTransitioning, setIsTransitioning] = useState(false);

  // Número de cards visíveis por vez (responsivo)
  const getVisibleCards = () => {
    if (window.innerWidth < 768) return 1;
    if (window.innerWidth < 1024) return 2;
    return 3;
  };

  const [visibleCards, setVisibleCards] = useState(getVisibleCards());

  // Atualiza número de cards visíveis quando o tamanho da janela muda
  useEffect(() => {
    const handleResize = () => {
      setVisibleCards(getVisibleCards());
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  // Calcula o número máximo de slides
  const maxSlides = Math.max(0, certificacoesData.length - visibleCards);

  // Avança para o próximo slide
  const nextSlide = () => {
    if (!isTransitioning) {
      setIsTransitioning(true);
      setCurrentSlide((prev) => (prev >= maxSlides ? 0 : prev + 1));
      setTimeout(() => setIsTransitioning(false), 500);
    }
  };

  // Volta ao slide anterior
  const prevSlide = () => {
    if (!isTransitioning) {
      setIsTransitioning(true);
      setCurrentSlide((prev) => (prev <= 0 ? maxSlides : prev - 1));
      setTimeout(() => setIsTransitioning(false), 500);
    }
  };

  // Avança automaticamente os slides a cada 7 segundos
  useEffect(() => {
    const interval = setInterval(() => {
      nextSlide();
    }, 7000);

    return () => clearInterval(interval);
  }, [currentSlide, maxSlides]);

  // Suporte para gestos de toque (swipe)
  const handleTouchStart = (e) => {
    setTouchStart(e.targetTouches[0].clientX);
  };

  const handleTouchMove = (e) => {
    setTouchEnd(e.targetTouches[0].clientX);
  };

  const handleTouchEnd = () => {
    if (touchStart - touchEnd > 50) {
      // Swipe para esquerda
      nextSlide();
    }

    if (touchStart - touchEnd < -50) {
      // Swipe para direita
      prevSlide();
    }
  };

  const certificacoesRef = useRef(null);

  return (
    <section
      id="certificacoes"
      className="certificacoes"
      ref={certificacoesRef}
    >
      <div className="certificacoes-container">
        <div className="certificacoes-heading-wrapper">
          <div>
            <h3>EXCELÊNCIA EM ESTÉTICA</h3>
            <h1>Certificações e Especializações</h1>
          </div>
        </div>
        <p className="certificacoes-descricao">
          Conheça as certificações, especializações e cursos realizados por
          nossa equipe para oferecer os melhores tratamentos com as técnicas
          mais modernas e eficazes da estética facial e corporal.
        </p>

        <div className="certificacoes-carrossel-container">
          <button
            className="certificacoes-carrossel-btn prev"
            onClick={prevSlide}
            aria-label="Certificação anterior"
          >
            <FontAwesomeIcon icon={faChevronLeft} />
          </button>

          <div
            className="certificacoes-carrossel-view"
            onTouchStart={handleTouchStart}
            onTouchMove={handleTouchMove}
            onTouchEnd={handleTouchEnd}
          >
            <div
              className={`certificacoes-carrossel-track ${
                isTransitioning ? "transitioning" : ""
              }`}
              style={{
                transform: `translateX(calc(-${
                  (currentSlide * 100) / visibleCards
                }%))`,
                gridTemplateColumns: `repeat(${
                  certificacoesData.length
                }, calc(${100 / visibleCards}% - 20px))`,
                minHeight: "320px",
              }}
            >
              {certificacoesData.map((cert) => (
                <div className="certificacao-card" key={cert.id}>
                  <div className="certificacao-img">
                    <img
                      src={cert.imagem}
                      alt={`Certificado: ${cert.titulo}`}
                      onError={(e) => {
                        e.target.onerror = null;
                        e.target.src =
                          "https://via.placeholder.com/400x300?text=Certificado";
                      }}
                    />
                  </div>
                  <div className="certificacao-info">
                    <h3>Curso</h3>
                    <div className="certificacao-detalhes">
                      <p className="instituicao">
                        <FontAwesomeIcon icon={faGraduationCap} />
                        <span>Instituição</span>
                      </p>
                      <p className="ano">{cert.ano}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <button
            className="certificacoes-carrossel-btn next"
            onClick={nextSlide}
            aria-label="Próxima certificação"
          >
            <FontAwesomeIcon icon={faChevronRight} />
          </button>
        </div>
      </div>
    </section>
  );
};

export default Certificacoes;
