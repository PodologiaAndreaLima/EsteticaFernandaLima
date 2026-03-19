import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../components/site/Header";
import api from "../../services/api";
import "./AuthStyles.css";

// Importe as imagens usando importação estática que funciona com o Vite
import calendarIcon from "../../assets/Tear-Off Calendar.png";
import vectorIcon from "../../assets/Vector.png";
import lightIcon from "../../assets/Light.png";

const ForgotPassword = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    setEmail(e.target.value);
    if (error) setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!email.trim()) {
      setError("Por favor, insira seu email.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const normalizedEmail = email.trim().toLowerCase();

      // Chama o endpoint para solicitar verificação de email
      // O backend deve enviar um código OTP para o email do funcionário
      const response = await api.post("/usuarios/forgot-password", {
        email: normalizedEmail,
      });

      if (response.status === 200) {
        setSuccess(true);
        // Redireciona para a tela de reset password após 2 segundos
        setTimeout(() => {
          navigate("/reset-password", {
            state: { email: normalizedEmail },
          });
        }, 2000);
      }
    } catch (error) {
      console.error("Erro ao solicitar reset de senha:", error);
      const errorMessage =
        error.response?.data?.message ||
        error.response?.data?.error ||
        "Erro ao processar solicitação. Verifique o email e tente novamente.";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <>
        <Header />
        <main className="auth-container">
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

          <div className="auth-right">
            <div className="login-content">
              <h2>Verificação de Email</h2>
              <div className="success-message">
                <p>✓ Código enviado com sucesso!</p>
                <p>
                  Verificamos seu email e enviamos um código de verificação.
                </p>
              </div>
              <p className="redirect-text">
                Redirecionando para a próxima etapa...
              </p>
            </div>
          </div>
        </main>
      </>
    );
  }

  return (
    <>
      <Header />
      <main className="auth-container">
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

        <div className="auth-right">
          <div className="login-content">
            <h2>Recuperar Senha</h2>
            <p className="subtitle">
              Insira seu email para receber um código de verificação
            </p>

            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit}>
              <label>Email:</label>
              <input
                type="email"
                value={email}
                onChange={handleChange}
                placeholder="seu.email@example.com"
                required
                disabled={loading}
              />
              <button type="submit" className="btn-login" disabled={loading}>
                {loading ? "Verificando..." : "Receber Código"}
              </button>
            </form>

            <p className="create-account">
              Lembrou sua senha? <Link to="/login">Voltar ao Login</Link>
            </p>
          </div>
        </div>
      </main>
    </>
  );
};

export default ForgotPassword;
