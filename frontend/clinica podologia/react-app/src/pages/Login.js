import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../components/Header";
import "./AuthStyles.css";

// Importe as imagens - usando importação dinâmica para evitar problemas com espaços nos nomes dos arquivos
const calendarIcon = require("../assets/Tear-Off Calendar.png");
const vectorIcon = require("../assets/Vector.png");
const lightIcon = require("../assets/Light.png");

const Login = () => {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({ identifier: "", senha: "" });
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials((c) => ({ ...c, [name]: value }));
    if (error) setError("");
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setIsSubmitting(true);

    // Simular login de administrador: armazenar usuário e token localmente
    localStorage.setItem("token", "admin-token-simulated");
    localStorage.setItem(
      "user",
      JSON.stringify({
        id: "admin-1",
        nomeCompleto: "Administrador",
        email: credentials.identifier || "admin@clinica.com",
        cargo: "Administrador",
        isStaff: true,
        isAdmin: true,
      })
    );

    // Simular atraso e navegar para /admin
    setTimeout(() => {
      setIsSubmitting(false);
      navigate("/admin");
    }, 800);
  };

  return (
    <>
      <Header />
      <main className="auth-container">
        {/* LEFT PANEL */}
        <div className="auth-left">
          <div className="login-content">
            <h5>ÁREA RESTRITA</h5>
            <h1>Portal de acesso para equipe</h1>
            <p>Com seu acesso ao sistema, você pode:</p>
            <ul>
              <li>
                <img src={calendarIcon} alt="" className="icon-calendar" />
                Gerenciar agendamentos de pacientes
              </li>
              <li>
                <img src={vectorIcon} alt="" className="icon-clock" />
                Registrar e acessar histórico de tratamentos
              </li>
              <li>
                <img src={lightIcon} alt="" />
                Manter cadastro atualizado de clientes
              </li>
            </ul>
          </div>
        </div>

        {/* RIGHT PANEL */}
        <div className="auth-right">
          <div className="login-content">
            <h2>Acesso de Funcionários</h2>
            {error && (
              <div
                style={{
                  color: "#d32f2f",
                  marginBottom: "15px",
                  textAlign: "center",
                }}
              >
                {error}
              </div>
            )}
            <form onSubmit={handleSubmit}>
              <label>Email:</label>
              <input
                type="text"
                name="identifier"
                value={credentials.identifier}
                onChange={handleChange}
                placeholder="Insira seu email de funcionário"
                required
              />
              <label>Senha:</label>
              <input
                type="password"
                name="senha"
                value={credentials.senha}
                onChange={handleChange}
                placeholder="Insira sua senha"
                required
                disabled={isSubmitting}
              />
              <button
                type="submit"
                className="btn-login"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Entrando..." : "Entrar"}
              </button>
            </form>
            <p className="create-account">
              PRIMEIRO ACESSO? <Link to="/register">SOLICITAR CADASTRO</Link>
            </p>
            <p className="whatsapp-text">
              Esqueceu sua senha? Entre em contato com a administração
            </p>
          </div>
        </div>
      </main>
    </>
  );
};

export default Login;
