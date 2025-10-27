import React from "react";
import { Link } from "react-router-dom";
import "./PaginaInicial.css";

const PaginaInicial = () => {
  // Definir nome padrão para saudação para visualização sem autenticação
  const firstName = "Seja Bem-vindo";

  // Cards de acesso rápido com ícones
  const quickAccessCards = [
    {
      id: 1,
      title: "Ordem de Serviço",
      iconSrc: "/assets/iconeOrdemServico-sistema.png",
      path: "/sistema/ordem-servico",
      color: "#654576",
    },
    {
      id: 2,
      title: "Dashboard",
      iconSrc: "/assets/iconeDashboard-sistema.png",
      path: "/sistema/dashboard",
      color: "#654576",
    },
    {
      id: 3,
      title: "Clientes",
      iconSrc: "/assets/iconeClientes-sistema.png",
      path: "/sistema/clientes",
      color: "#654576",
    },
    {
      id: 4,
      title: "Serviços",
      iconSrc: "/assets/iconeServico-sistema.png",
      path: "/sistema/servicos",
      color: "#654576",
    },
    {
      id: 5,
      title: "Produtos",
      iconSrc: "/assets/iconeProduto-sistema.png",
      path: "/sistema/produtos",
      color: "#654576",
    },
    {
      id: 6,
      title: "Combos",
      iconSrc: "/assets/iconeCombo-sistema.png",
      path: "/sistema/combos",
      color: "#654576",
    },
    {
      id: 7,
      title: "Funcionários",
      iconSrc: "/assets/iconeFuncionario-sistema.png",
      path: "/sistema/funcionarios",
      color: "#654576",
    },
    {
      id: 8,
      title: "Custos",
      iconSrc: "/assets/iconeCustos-sistema.png",
      path: "/sistema/custos",
      color: "#654576",
    },
  ];

  return (
    <div className="pagina-inicial">
      <div className="welcome-section">
        <h1>Olá, {firstName}!</h1>
      </div>

      <div className="quick-access-grid">
        {quickAccessCards.map((card) => (
          <Link
            to={card.path}
            className="quick-access-card"
            key={card.id}
            style={{ backgroundColor: card.color }}
          >
            <div className="card-icon">
              <img
                src={card.iconSrc}
                alt={card.title}
                className="card-icon-image"
              />
            </div>
            <div className="card-title">{card.title}</div>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default PaginaInicial;
