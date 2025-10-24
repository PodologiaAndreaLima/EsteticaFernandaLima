import React, { useState, useEffect } from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import "./Header.css";
// Importando imagens da utility
import images from "../../utils/images.js";

const Header = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  // Detectar quando a página é rolada para adicionar classe ao header
  useEffect(() => {
    const handleWindowScroll = () => {
      if (window.scrollY > 50) {
        setScrolled(true);
      } else {
        setScrolled(false);
      }
    };

    // Adicionar event listener para scroll
    window.addEventListener("scroll", handleWindowScroll);

    // Limpar event listener quando o componente é desmontado
    return () => {
      window.removeEventListener("scroll", handleWindowScroll);
    };
  }, []);

  const handleScroll = (id) => {
    // Se estamos na página inicial, role para o elemento
    if (location.pathname === "/") {
      const element = document.getElementById(id);
      if (element) {
        element.scrollIntoView({ behavior: "smooth" });
      }
    }
    // Fechar o menu mobile após clicar em um link
    setMobileMenuOpen(false);
  };

  const toggleMobileMenu = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  return (
    <header className={scrolled ? "scrolled" : ""}>
      <div className="header-container">
        <div className="logo-menu-container">
          <div className="logo">
            <Link to="/">
              <img
                src="/assets/fotoLogoRoxo-removebg-preview.png"
                alt="Logo Clínica Fernanda Beauty Care"
                className="clinic-logo"
              />
            </Link>
          </div>
          <button
            className="mobile-menu-toggle"
            onClick={toggleMobileMenu}
            aria-label="Toggle menu"
          >
            <span
              className={`burger-icon ${mobileMenuOpen ? "open" : ""}`}
            ></span>
          </button>
        </div>

        <nav className={mobileMenuOpen ? "nav-open" : ""}>
          <ul>
            <li>
              <a href="#servicos" onClick={() => handleScroll("servicos")}>
                Serviços
              </a>
            </li>
            <li>
              <a href="#sobre-nos" onClick={() => handleScroll("sobre-nos")}>
                Sobre Nós
              </a>
            </li>
            <li>
              <a href="#relatos" onClick={() => handleScroll("relatos")}>
                Relatos
              </a>
            </li>
            <li>
              <a href="#contato" onClick={() => handleScroll("contato")}>
                Contato
              </a>
            </li>
          </ul>
        </nav>

        <div className="header-buttons">
          <div className="auth-buttons">
            <a
              href="https://api.whatsapp.com/send?phone=5511999999999"
              target="_blank"
              rel="noopener noreferrer"
              className="btn-agendar"
            >
              Agende já sua consulta!
            </a>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
