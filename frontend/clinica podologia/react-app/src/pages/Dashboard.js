import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import Header from "../components/Header";
import { UserService, AppointmentService } from "../services";
import "../styles/Dashboard.css";

const Dashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("pacientes");
  const [loading, setLoading] = useState(true);
  const [userDetails, setUserDetails] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    // Redireciona para login se não estiver autenticado
    if (!user) {
      navigate("/");
      return;
    }

    // Verifica se o usuário é um funcionário (apenas funcionários podem acessar)
    if (!user.isStaff) {
      setError("Acesso restrito a funcionários");
      logout();
      navigate("/");
      return;
    }

    // Busca dados detalhados do funcionário e os agendamentos
    const fetchUserData = async () => {
      setLoading(true);
      try {
        // Aqui buscaríamos os dados relevantes para o dashboard do funcionário
        setUserDetails(user); // Por enquanto usamos os dados básicos do contexto

        // Busca agendamentos gerais da clínica (não apenas do usuário)
        const appointmentsResponse =
          await AppointmentService.getAllAppointments();

        if (appointmentsResponse.success) {
          setAppointments(appointmentsResponse.appointments || []);
        }
      } catch (err) {
        console.error("Erro ao buscar dados:", err);
        setError("Ocorreu um erro ao buscar os dados necessários.");
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, [user, navigate, logout]);

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const formatDate = (dateString) => {
    if (!dateString) return "";
    const options = {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    };
    return new Date(dateString).toLocaleDateString("pt-BR", options);
  };

  const getStatusClass = (status) => {
    switch (status?.toLowerCase()) {
      case "confirmado":
        return "status-confirmed";
      case "pendente":
        return "status-pending";
      case "cancelado":
        return "status-canceled";
      default:
        return "";
    }
  };

  if (!user || !user.isStaff) {
    return <div>Verificando permissões...</div>;
  }

  return (
    <>
      <Header />
      <div className="dashboard-container">
        <aside className="dashboard-sidebar">
          <div className="sidebar-user">
            <div className="user-info">
              <h3>{userDetails?.nomeCompleto || user?.nomeCompleto}</h3>
              <p>{userDetails?.cargo || user?.cargo}</p>
            </div>
          </div>

          <nav className="sidebar-nav">
            <ul>
              <li className={activeTab === "pacientes" ? "active" : ""}>
                <button onClick={() => setActiveTab("pacientes")}>
                  <i className="fas fa-users"></i> Pacientes
                </button>
              </li>
              <li className={activeTab === "agendamentos" ? "active" : ""}>
                <button onClick={() => setActiveTab("agendamentos")}>
                  <i className="fas fa-calendar-alt"></i> Agendamentos
                </button>
              </li>
              <li className={activeTab === "procedimentos" ? "active" : ""}>
                <button onClick={() => setActiveTab("procedimentos")}>
                  <i className="fas fa-clipboard-list"></i> Procedimentos
                </button>
              </li>
              <li className={activeTab === "financeiro" ? "active" : ""}>
                <button onClick={() => setActiveTab("financeiro")}>
                  <i className="fas fa-dollar-sign"></i> Financeiro
                </button>
              </li>
              <li className={activeTab === "perfil" ? "active" : ""}>
                <button onClick={() => setActiveTab("perfil")}>
                  <i className="fas fa-user-cog"></i> Meu Perfil
                </button>
              </li>
              <li>
                <button onClick={handleLogout} className="logout-button">
                  <i className="fas fa-sign-out-alt"></i> Sair
                </button>
              </li>
            </ul>
          </nav>
        </aside>

        <main className="dashboard-content">
          {loading ? (
            <div className="loading">Carregando dados...</div>
          ) : error ? (
            <div className="error-message">{error}</div>
          ) : (
            <>
              {activeTab === "pacientes" && (
                <section className="dashboard-section">
                  <h2>Gerenciamento de Pacientes</h2>
                  <div className="section-actions">
                    <button className="action-button">
                      <i className="fas fa-plus"></i> Novo Paciente
                    </button>
                    <div className="search-box">
                      <input type="text" placeholder="Buscar paciente..." />
                      <button>
                        <i className="fas fa-search"></i>
                      </button>
                    </div>
                  </div>

                  <div className="data-table">
                    <p>Lista de pacientes será exibida aqui</p>
                  </div>
                </section>
              )}

              {activeTab === "agendamentos" && (
                <section className="dashboard-section">
                  <h2>Agenda de Atendimentos</h2>
                  <div className="section-actions">
                    <button className="action-button">
                      <i className="fas fa-plus"></i> Novo Agendamento
                    </button>
                    <div className="date-picker">
                      <input type="date" />
                    </div>
                  </div>

                  {appointments.length === 0 ? (
                    <div className="no-appointments">
                      <p>Não há agendamentos registrados.</p>
                    </div>
                  ) : (
                    <div className="appointments-list">
                      {/* Aqui entrariam os agendamentos quando implementados */}
                      <p>Calendário de agendamentos será exibido aqui</p>
                    </div>
                  )}
                </section>
              )}

              {activeTab === "procedimentos" && (
                <section className="dashboard-section">
                  <h2>Procedimentos Estéticos</h2>
                  <div className="procedures-list">
                    <p>
                      Lista de procedimentos estéticos disponíveis será exibida
                      aqui
                    </p>
                  </div>
                </section>
              )}

              {activeTab === "financeiro" && (
                <section className="dashboard-section">
                  <h2>Relatórios Financeiros</h2>
                  <div className="financial-summary">
                    <p>Resumo financeiro será exibido aqui</p>
                  </div>
                </section>
              )}

              {activeTab === "perfil" && (
                <section className="dashboard-section">
                  <h2>Meu Perfil</h2>
                  <div className="profile-form">
                    {userDetails && (
                      <div className="profile-details">
                        <div className="details-item">
                          <strong>Nome:</strong> {userDetails.nomeCompleto}
                        </div>
                        <div className="details-item">
                          <strong>Email:</strong> {userDetails.email}
                        </div>
                        <div className="details-item">
                          <strong>CPF:</strong> {userDetails.cpf}
                        </div>
                        <div className="details-item">
                          <strong>Telefone:</strong> {userDetails.telefone}
                        </div>
                        <div className="details-item">
                          <strong>Cargo:</strong> {userDetails.cargo}
                        </div>
                      </div>
                    )}
                  </div>
                </section>
              )}
            </>
          )}
        </main>
      </div>
    </>
  );
};

export default Dashboard;
