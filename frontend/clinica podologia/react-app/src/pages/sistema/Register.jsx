import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Header from "../../components/site/Header";
import { useAuth } from "../../contexts/AuthContext";
import {
  buildPasswordPolicyMessage,
  validateStrongPassword,
} from "../../utils/authErrorUtils";
import "./AuthStyles.css";

// Importe as imagens usando importação estática que funciona com o Vite
import calendarIcon from "../../assets/Tear-Off Calendar.png";
import vectorIcon from "../../assets/Vector.png";
import lightIcon from "../../assets/Light.png";

const Register = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nomeCompleto: "",
    cpf: "",
    telefone: "",
    email: "",
    cargo: "",
    senha: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Função para formatar telefone no padrão exigido pelo backend (DD) XXXXX-XXXX
  const formatTelefone = (telefone) => {
    // Remove todos os caracteres não numéricos
    const numeros = telefone.replace(/\D/g, "");

    // Se tiver números suficientes, aplica a formatação
    if (numeros.length >= 10) {
      const ddd = numeros.substring(0, 2);
      const parte1 = numeros.substring(2, numeros.length === 10 ? 6 : 7);
      const parte2 = numeros.substring(numeros.length === 10 ? 6 : 7, 11);
      return `(${ddd}) ${parte1}-${parte2}`;
    }

    // Caso contrário, retorna o que foi digitado
    return telefone;
  };

  // Função para formatar CPF (apenas remove caracteres não numéricos)
  const formatCpf = (cpf) => {
    return cpf.replace(/\D/g, "");
  };

  // Função para formatar cargo
  const [cargos] = useState([
    "Esteticista",
    "Recepcionista",
    "Gerente",
    "Administração",
    "Outro",
  ]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    // Aplica formatação específica conforme o campo
    let formattedValue = value;

    if (name === "telefone") {
      formattedValue = formatTelefone(value);
    } else if (name === "cpf") {
      formattedValue = formatCpf(value);
    }

    setFormData({
      ...formData,
      [name]: formattedValue,
    });

    // Limpar mensagem de erro quando o usuário começa a digitar
    if (error) setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError("");

    // Validação básica
    if (formData.senha !== formData.confirmPassword) {
      setError("As senhas não coincidem!");
      setIsSubmitting(false);
      return;
    }

    const passwordValidation = validateStrongPassword(formData.senha);
    if (!passwordValidation.isValid) {
      setError(buildPasswordPolicyMessage(passwordValidation.missing));
      setIsSubmitting(false);
      return;
    }

    // Validar formato do telefone
    const telefonePattern = /^\(\d{2}\) \d{4,5}-\d{4}$/;
    if (!telefonePattern.test(formData.telefone)) {
      setError("Telefone deve estar no formato (DD) XXXXX-XXXX");
      setIsSubmitting(false);
      return;
    }

    // Validar CPF (remover formatação se tiver)
    const cpfLimpo = formData.cpf.replace(/\D/g, "");
    if (cpfLimpo.length !== 11) {
      setError("CPF deve conter 11 dígitos");
      setIsSubmitting(false);
      return;
    }

    // Preparar o objeto para enviar à API (sem a senha de confirmação)
    const userData = {
      nomeCompleto: formData.nomeCompleto,
      cpf: cpfLimpo, // CPF sem formatação
      telefone: formData.telefone, // Telefone já formatado pelo handleChange
      email: formData.email,
      cargo: formData.cargo,
      senha: formData.senha,
      isStaff: true, // Marca explicitamente que é funcionário
    };

    try {
      const result = await register(userData);

      if (result.success) {
        // Redirecionar para o dashboard após registro bem-sucedido
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

  return (
    <>
      <Header />
      <main className="auth-container">
        {/* LEFT PANEL */}
        <div className="auth-left cadastro">
          <div className="cadastro-content">
            <h5>ÁREA RESTRITA</h5>
            <h1>Cadastro de funcionários da clínica</h1>
            <p>Nossos colaboradores podem:</p>
            <ul>
              <li>
                <img src={calendarIcon} alt="" />
                Cadastrar e gerenciar pacientes
              </li>
              <li>
                <img src={vectorIcon} alt="" />
                Controlar agendamentos e procedimentos
              </li>
              <li>
                <img src={lightIcon} alt="" />
                Acompanhar histórico de tratamentos
              </li>
            </ul>
          </div>
        </div>

        {/* RIGHT PANEL */}
        <div className="auth-right">
          <div className="cadastro-content">
            <h2>Cadastro de funcionário</h2>
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
              <label>Nome completo:</label>
              <input
                type="text"
                name="nomeCompleto"
                value={formData.nomeCompleto}
                onChange={handleChange}
                placeholder="Nome do funcionário"
                required
              />
              <div className="input-duo">
                <div>
                  <label>CPF:</label>
                  <input
                    type="text"
                    name="cpf"
                    value={formData.cpf}
                    onChange={handleChange}
                    placeholder="Apenas números"
                    required
                    maxLength="11"
                  />
                </div>
                <div>
                  <label>Telefone:</label>
                  <input
                    type="text"
                    name="telefone"
                    value={formData.telefone}
                    onChange={handleChange}
                    placeholder="(DD) XXXXX-XXXX"
                    required
                  />
                </div>
              </div>
              <label>Email:</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="Insira seu email"
                required
              />
              <label>Cargo:</label>
              <select
                name="cargo"
                value={formData.cargo}
                onChange={handleChange}
                required
                className="select-cargo"
              >
                <option value="" disabled>
                  Selecione o cargo
                </option>
                {cargos.map((cargo, index) => (
                  <option key={index} value={cargo}>
                    {cargo}
                  </option>
                ))}
              </select>

              <div className="input-duo">
                <div>
                  <label>Senha:</label>
                  <input
                    type="password"
                    name="senha"
                    value={formData.senha}
                    onChange={handleChange}
                    placeholder="Defina uma senha"
                    required
                  />
                </div>
                <div>
                  <label>Confirmar senha:</label>
                  <input
                    type="password"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    placeholder="Confirme a senha"
                    required
                  />
                </div>
              </div>
              <p
                className="auth-note"
                style={{
                  fontSize: "12px",
                  color: "#666",
                  fontStyle: "italic",
                  marginBottom: "15px",
                  textAlign: "center",
                }}
              >
                Politica de senha: minimo 8 caracteres, com letra maiuscula,
                letra minuscula, numero e caractere especial.
                <br />
                Este cadastro será analisado pela administração antes da
                aprovação
              </p>
              <button
                type="submit"
                className="btn-cadastro"
                disabled={isSubmitting}
              >
                {isSubmitting
                  ? "Enviando solicitação..."
                  : "Solicitar cadastro"}
              </button>
            </form>
            <p className="login-link">
              JÁ POSSUI ACESSO? <Link to="/">FAZER LOGIN</Link>
            </p>
          </div>
        </div>
      </main>
    </>
  );
};

export default Register;
