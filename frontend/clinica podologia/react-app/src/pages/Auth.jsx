import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import { useAuth } from "../contexts/AuthContext";
import "./AuthStyles.css";

// Imagens
import calendarIcon from "../assets/Tear-Off Calendar.png";
import vectorIcon from "../assets/Vector.png";
import lightIcon from "../assets/Light.png";

const Auth = () => {
  const { login, register } = useAuth();
  const navigate = useNavigate();
  const [isLoginForm, setIsLoginForm] = useState(true);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Estado para o formulário de login
  const [loginCredentials, setLoginCredentials] = useState({
    identifier: "",
    password: "",
  });

  // Estado para o formulário de registro
  const [registerData, setRegisterData] = useState({
    fullName: "",
    cpf: "",
    phone: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  // Manipuladores para o formulário de login
  const handleLoginChange = (e) => {
    const { name, value } = e.target;
    setLoginCredentials({
      ...loginCredentials,
      [name]: value,
    });
    if (error) setError("");
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError("");

    try {
      const result = await login(
        loginCredentials.identifier,
        loginCredentials.password
      );

      if (result.success) {
        navigate("/dashboard");
      } else {
        setError(result.error || "Falha ao fazer login");
      }
    } catch (err) {
      setError("Ocorreu um erro durante o login. Tente novamente.");
      console.error("Erro no login:", err);
    } finally {
      setIsSubmitting(false);
    }
  };

  // Manipuladores para o formulário de registro
  const handleRegisterChange = (e) => {
    const { name, value } = e.target;
    setRegisterData({
      ...registerData,
      [name]: value,
    });
    if (error) setError("");
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError("");

    // Validação básica
    if (registerData.password !== registerData.confirmPassword) {
      setError("As senhas não coincidem!");
      setIsSubmitting(false);
      return;
    }

    try {
      const result = await register(registerData);

      if (result.success) {
        navigate("/dashboard");
      } else {
        setError(result.error || "Falha ao criar conta");
      }
    } catch (err) {
      setError("Ocorreu um erro durante o cadastro. Tente novamente.");
      console.error("Erro no cadastro:", err);
    } finally {
      setIsSubmitting(false);
    }
  };

  // Alternar entre os formulários
  const toggleForm = (formType) => {
    setIsLoginForm(formType === "login");
    setError("");
  };

  return (
    <>
      <Header />
      <main className="auth-container">
        {/* LEFT PANEL */}
        <div
          className={`auth-left ${
            isLoginForm ? "login-active" : "cadastro-active"
          }`}
        >
          <div className={`login-content ${isLoginForm ? "active" : ""}`}>
            <h5>FAÇA NO SEU RITMO</h5>
            <h1>Estamos aqui para te atender da melhor forma!</h1>
            <p>Ao fazer parte do nosso sistema, você pode:</p>
            <ul>
              <li>
                <img src={calendarIcon} alt="" className="icon-calendar" />
                Agendar consultas por conta própria
              </li>
              <li>
                <img src={vectorIcon} alt="" className="icon-clock" />
                Acompanhar históricos e consultas futuras
              </li>
              <li>
                <img src={lightIcon} alt="" />
                Receber dicas sobre cuidados com seus pés
              </li>
            </ul>
          </div>
          <div className={`cadastro-content ${!isLoginForm ? "active" : ""}`}>
            <h5>BOAS VINDAS!</h5>
            <h1>Conheça as vantagens de criar uma conta conosco</h1>
            <p>Criando sua conta, você poderá:</p>
            <ul>
              <li>
                <img src={calendarIcon} alt="" />
                Agendar consultas por conta própria
              </li>
              <li>
                <img src={vectorIcon} alt="" />
                Acompanhar históricos e consultas futuras
              </li>
              <li>
                <img src={lightIcon} alt="" />
                Receber dicas sobre cuidados com seus pés
              </li>
            </ul>
          </div>
        </div>

        {/* RIGHT PANEL */}
        <div
          className={`auth-right ${
            isLoginForm ? "login-active" : "cadastro-active"
          }`}
        >
          {/* LOGIN FORM */}
          <div className={`login-content ${isLoginForm ? "active" : ""}`}>
            <h2>Entre na sua conta</h2>
            {error && isLoginForm && (
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
            <form onSubmit={handleLoginSubmit}>
              <label>Email ou CPF:</label>
              <input
                type="text"
                name="identifier"
                value={loginCredentials.identifier}
                onChange={handleLoginChange}
                placeholder="Insira seu Email ou CPF"
                required
                disabled={isSubmitting}
              />
              <label>Senha:</label>
              <input
                type="password"
                name="password"
                value={loginCredentials.password}
                onChange={handleLoginChange}
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
              NÃO POSSUI UMA CONTA?{" "}
              <button
                className="link-button"
                onClick={() => toggleForm("cadastro")}
              >
                CRIAR CONTA
              </button>
            </p>
            <p className="whatsapp-text">
              Se preferir, fale conosco direto pelo Whatsapp
            </p>
            <button className="btn-whatsapp">Agendar via Whatsapp</button>
          </div>

          {/* REGISTER FORM */}
          <div className={`cadastro-content ${!isLoginForm ? "active" : ""}`}>
            <h2>Faça parte agora mesmo!</h2>
            {error && !isLoginForm && (
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
            <form onSubmit={handleRegisterSubmit}>
              <label>Nome completo:</label>
              <input
                type="text"
                name="fullName"
                value={registerData.fullName}
                onChange={handleRegisterChange}
                placeholder="Insira seu nome completo"
                required
                disabled={isSubmitting}
              />
              <div className="input-duo">
                <div>
                  <label>CPF:</label>
                  <input
                    type="text"
                    name="cpf"
                    value={registerData.cpf}
                    onChange={handleRegisterChange}
                    placeholder="Insira seu CPF"
                    required
                    disabled={isSubmitting}
                  />
                </div>
                <div>
                  <label>Telefone:</label>
                  <input
                    type="text"
                    name="phone"
                    value={registerData.phone}
                    onChange={handleRegisterChange}
                    placeholder="Insira seu número"
                    required
                    disabled={isSubmitting}
                  />
                </div>
              </div>
              <label>Email:</label>
              <input
                type="email"
                name="email"
                value={registerData.email}
                onChange={handleRegisterChange}
                placeholder="Insira seu email"
                required
                disabled={isSubmitting}
              />
              <div className="input-duo">
                <div>
                  <label>Senha:</label>
                  <input
                    type="password"
                    name="password"
                    value={registerData.password}
                    onChange={handleRegisterChange}
                    placeholder="Insira sua senha"
                    required
                    disabled={isSubmitting}
                  />
                </div>
                <div>
                  <label>Confirmar senha:</label>
                  <input
                    type="password"
                    name="confirmPassword"
                    value={registerData.confirmPassword}
                    onChange={handleRegisterChange}
                    placeholder="Confirme sua senha"
                    required
                    disabled={isSubmitting}
                  />
                </div>
              </div>
              <button
                type="submit"
                className="btn-cadastro"
                disabled={isSubmitting}
              >
                {isSubmitting ? "Criando conta..." : "Criar conta"}
              </button>
            </form>
            <p className="login-link">
              JÁ POSSUI UMA CONTA?{" "}
              <button
                className="link-button"
                onClick={() => toggleForm("login")}
              >
                FAZER LOGIN
              </button>
            </p>
          </div>
        </div>
      </main>
    </>
  );
};

export default Auth;
