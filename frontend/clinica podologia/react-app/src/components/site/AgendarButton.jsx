import React from "react";
import { Link } from "react-router-dom";
import "./AgendarButton.css";

const AgendarButton = ({
  to = "/agendar",
  external = false,
  text = "Agendar",
  className = "",
  onClick,
}) => {
  const content = <span className="agendar-button__text">{text}</span>;

  if (external) {
    return (
      <a
        href={to}
        className={`agendar-button ${className}`}
        onClick={onClick}
        target="_blank"
        rel="noopener noreferrer"
      >
        {content}
      </a>
    );
  }

  return (
    <Link to={to} className={`agendar-button ${className}`} onClick={onClick}>
      {content}
    </Link>
  );
};

export default AgendarButton;
