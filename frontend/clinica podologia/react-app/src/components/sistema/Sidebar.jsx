import React, { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import "./Sidebar.css";

const Sidebar = () => {
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);
  const { logout } = useAuth();
  const navigate = useNavigate();

  // Verificar qual rota está ativa
  const isActive = (path) => {
    return location.pathname === path;
  };

  // Toggle para expandir/colapsar a sidebar
  const toggleSidebar = () => {
    setCollapsed(!collapsed);
  };

  // Função para logout (temporariamente redirecionando para a página inicial)
  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className={`sidebar ${collapsed ? "collapsed" : ""}`}>
      {/* Botão de toggle da sidebar - posicionado fora da estrutura normal */}
      <button onClick={toggleSidebar} className="sidebar-toggle">
        <div className="hamburger-icon">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </button>

      <div className="sidebar-header">
        {/* Logo ou Avatar - substituído por logo da clínica */}
        <div className="sidebar-logo">
          <img
            src="/assets/fotoLogoRoxo-removebg-preview.png"
            alt="Logo Clínica Fernanda Lima"
            className="sidebar-logo__img"
          />
        </div>
      </div>

      <div className="sidebar-menu">
        <ul>
          <li className={isActive("/sistema") ? "active" : ""}>
            <Link to="/sistema">
              <img
                src="/assets/iconePaginaInicial-sistema.png"
                alt="Página Inicial"
              />
              <span>Página Inicial</span>
            </Link>
          </li>
          <li className={isActive("/sistema/ordem-servico") ? "active" : ""}>
            <Link to="/sistema/ordem-servico">
              <img
                src="/assets/Ordem de Servico-Sidebar.png"
                alt="Ordem de Serviço"
              />
              <span>Ordem de Serviço</span>
            </Link>
          </li>
          <li className={isActive("/sistema/dashboard") ? "active" : ""}>
            <Link to="/sistema/dashboard">
              <img src="/assets/icone-dash-sidebar.png" alt="Dashboard" />
              <span>Dashboard</span>
            </Link>
          </li>
          <li className={isActive("/sistema/clientes") ? "active" : ""}>
            <Link to="/sistema/clientes">
              <img src="/assets/Clientes-Sidebar.png" alt="Clientes" />
              <span>Clientes</span>
            </Link>
          </li>
          <li className={isActive("/sistema/servicos") ? "active" : ""}>
            <Link to="/sistema/servicos">
              <img src="/assets/Servico-Sidebar.png" alt="Serviços" />
              <span>Serviços</span>
            </Link>
          </li>
          <li className={isActive("/sistema/produtos") ? "active" : ""}>
            <Link to="/sistema/produtos">
              <img src="/assets/Produtos-Sidebar.png" alt="Produtos" />
              <span>Produtos</span>
            </Link>
          </li>
          <li className={isActive("/sistema/combos") ? "active" : ""}>
            <Link to="/sistema/combos">
              <img src="/assets/Combo-Sidebar.png" alt="Combos" />
              <span>Combos</span>
            </Link>
          </li>
          <li className={isActive("/sistema/funcionarios") ? "active" : ""}>
            <Link to="/sistema/funcionarios">
              <img src="/assets/Funcionario-Sidebar.png" alt="Funcionários" />
              <span>Funcionários</span>
            </Link>
          </li>
          <li className={isActive("/sistema/custos") ? "active" : ""}>
            <Link to="/sistema/custos">
              <img src="/assets/Custos-Sidebar.png" alt="Custos" />
              <span>Custos</span>
            </Link>
          </li>
          <li className={isActive("/sistema/perfil") ? "active" : ""}>
            <Link to="/sistema/perfil">
              <img src="/assets/Perfil-Sidebar.png" alt="Perfil" />
              <span>Perfil</span>
            </Link>
          </li>
        </ul>
      </div>

      <div className="sidebar-footer">
        <button onClick={handleLogout} className="logout-button">
          <img src="/assets/iconeSair-sistema.svg" alt="Sair" />
          <span>Sair</span>
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
