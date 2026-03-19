import React, { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import Header from "../../components/site/Header";
import api from "../../services/api";
import "./AuthStyles.css";

// Importe as imagens usando importação estática que funciona com o Vite
import calendarIcon from "../../assets/Tear-Off Calendar.png";
import vectorIcon from "../../assets/Vector.png";
import lightIcon from "../../assets/Light.png";

const ResetPassword = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const emailFromState = location.state?.email || "";

  const [email, setEmail] = useState(emailFromState);
  const [code, setCode] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [step, setStep] = useState("code"); // "code" ou "password"

  const handleCodeChange = (e) => {
    setCode(e.target.value);
    if (error) setError("");
  };

  const handlePasswordChange = (e) => {
    setNewPassword(e.target.value);
    if (error) setError("");
  };

  const handleConfirmChange = (e) => {
    setConfirmPassword(e.target.value);
    if (error) setError("");
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
    if (error) setError("");
  };

  const validatePassword = (password) => {
    if (password.length < 6) {
      return "A senha deve ter no mínimo 6 caracteres.";
    }
    return null;
  };

  const handleVerifyCode = async (e) => {
    e.preventDefault();

    if (!email.trim() || !code.trim()) {
      setError("Por favor, preencha todos os campos.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const normalizedEmail = email.trim().toLowerCase();

      // Chama o endpoint para verificar o código OTP
      const response = await api.post("/usuarios/verify-reset-code", {
        email: normalizedEmail,
        code: code.trim(),
      });

      if (response.status === 200) {
        setStep("password");
      }
    } catch (error) {
      console.error("Erro ao verificar código:", error);
      const errorMessage =
        error.response?.data?.message ||
        error.response?.data?.error ||
        "Código inválido ou expirado. Tente novamente.";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();

    if (!newPassword || !confirmPassword) {
      setError("Por favor, preencha todos os campos de senha.");
      return;
    }

    const passwordError = validatePassword(newPassword);
    if (passwordError) {
      setError(passwordError);
      return;
    }

    if (newPassword !== confirmPassword) {
      setError("As senhas não conferem.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const normalizedEmail = email.trim().toLowerCase();

      // Chama o endpoint para redefinir a senha
      const response = await api.post("/usuarios/reset-password", {
        email: normalizedEmail,
        code: code.trim(),
        newPassword: newPassword,
      });

      if (response.status === 200) {
        // Redireciona para login após 2 segundos
        setTimeout(() => {
          navigate("/login", {
            state: {
              message:
                "Senha redefinida com sucesso! Faça login com sua nova senha.",
            },
          });
        }, 2000);
      }
    } catch (error) {
      console.error("Erro ao redefinir senha:", error);
      const errorMessage =
        error.response?.data?.message ||
        error.response?.data?.error ||
        "Erro ao redefinir senha. Tente novamente.";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  if (step === "password") {
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
              <h2>Nova Senha</h2>
              <p className="subtitle">Crie uma nova senha para sua conta</p>

              {error && <div className="error-message">{error}</div>}

              <form onSubmit={handleResetPassword}>
                <label>Nova Senha:</label>
                <div className="password-input-group">
                  <input
                    type={showPassword ? "text" : "password"}
                    value={newPassword}
                    onChange={handlePasswordChange}
                    placeholder="Digite sua nova senha"
                    required
                    disabled={loading}
                  />
                  <button
                    type="button"
                    className="btn-toggle-password"
                    onClick={() => setShowPassword(!showPassword)}
                    tabIndex="-1"
                  >
                    {showPassword ? "Ocultar" : "Mostrar"}
                  </button>
                </div>

                <label>Confirmar Senha:</label>
                <input
                  type={showPassword ? "text" : "password"}
                  value={confirmPassword}
                  onChange={handleConfirmChange}
                  placeholder="Confirme sua nova senha"
                  required
                  disabled={loading}
                />

                <button type="submit" className="btn-login" disabled={loading}>
                  {loading ? "Salvando..." : "Salvar Nova Senha"}
                </button>
              </form>

              <p className="create-account">
                <Link to="/login">Voltar ao Login</Link>
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
            <h2>Verificar Código</h2>
            <p className="subtitle">
              Insira o código de verificação enviado para {email}
            </p>

            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleVerifyCode}>
              <label>Email:</label>
              <input
                type="email"
                value={email}
                onChange={handleEmailChange}
                placeholder="seu.email@example.com"
                disabled={loading}
              />

              <label>Código de Verificação:</label>
              <input
                type="text"
                value={code}
                onChange={handleCodeChange}
                placeholder="Digite o código recebido"
                required
                disabled={loading}
              />

              <small
                style={{
                  color: "#666",
                  marginBottom: "15px",
                  display: "block",
                }}
              >
                Verifique sua caixa de entrada e pasta de spam. O código foi
                enviado para o email informado.
              </small>

              <button type="submit" className="btn-login" disabled={loading}>
                {loading ? "Verificando..." : "Verificar Código"}
              </button>
            </form>

            <p className="create-account">
              Não recebeu o código?{" "}
              <Link to="/forgot-password">Solicitar novamente</Link>
            </p>
          </div>
        </div>
      </main>
    </>
  );
};

export default ResetPassword;
