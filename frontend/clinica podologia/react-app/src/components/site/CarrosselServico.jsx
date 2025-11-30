import React, { useState, useEffect } from "react";
import "./CarrosselServico.css";

const Card = ({ service, index, hoveredIndex, setHoveredIndex }) => {
  const isHovered = hoveredIndex === index;
  const isBlurred = hoveredIndex !== null && !isHovered;

  return (
    <div
      className={`cs-card ${isHovered ? "hovered" : ""} ${
        isBlurred ? "blurred" : ""
      }`}
      onMouseEnter={() => setHoveredIndex(index)}
      onMouseLeave={() => setHoveredIndex(null)}
      role="button"
      tabIndex={0}
    >
      <div className="cs-image">
        <img src={service.img} alt={service.title} />
      </div>
      <div className="cs-body">
        <h3>{service.title}</h3>
        <p className="cs-desc">{service.desc}</p>
      </div>
    </div>
  );
};

const CarrosselServico = ({ services }) => {
  const [startIndex, setStartIndex] = useState(0);
  const [visible, setVisible] = useState(2);
  const [hoveredIndex, setHoveredIndex] = useState(null);

  useEffect(() => {
    const handleResize = () => {
      setVisible(window.innerWidth <= 768 ? 1 : 2);
    };
    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const maxStart = Math.max(0, services.length - visible);

  const prev = () =>
    setStartIndex((s) => (s === 0 ? maxStart : Math.max(0, s - 1)));
  const next = () =>
    setStartIndex((s) => (s >= maxStart ? 0 : Math.min(maxStart, s + 1)));

  // Autoplay: advance every X ms while user is NOT hovering a card. Loops when reaching the end.
  useEffect(() => {
    const intervalMs = 3000;
    if (hoveredIndex !== null) return; // paused while hovering a card

    const id = setInterval(() => {
      setStartIndex((s) => (s >= maxStart ? 0 : s + 1));
    }, intervalMs);

    return () => clearInterval(id);
  }, [hoveredIndex, maxStart]);

  const visibleServices = services.slice(startIndex, startIndex + visible);

  return (
    <div className="cs-wrapper">
      <button
        className="cs-arrow cs-prev"
        onClick={prev}
        aria-label="Anterior"
        disabled={startIndex === 0}
      >
        ‹
      </button>
      <div className="cs-viewport">
        <div className="cs-track">
          {visibleServices.map((s, i) => {
            const realIndex = startIndex + i;
            const itemStyle = {
              width: visible === 2 ? `calc(50% - 15px)` : `100%`,
            };
            return (
              <div key={realIndex} className="cs-track-item" style={itemStyle}>
                <Card
                  service={s}
                  index={realIndex}
                  hoveredIndex={hoveredIndex}
                  setHoveredIndex={setHoveredIndex}
                />
              </div>
            );
          })}
        </div>
      </div>
      <button
        className="cs-arrow cs-next"
        onClick={next}
        aria-label="Próximo"
        disabled={startIndex + visible >= services.length}
      >
        ›
      </button>
    </div>
  );
};

export default CarrosselServico;
