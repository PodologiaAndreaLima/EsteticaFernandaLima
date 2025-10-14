import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Home from "./pages/Home";
import Dashboard from "./pages/Dashboard";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import "./styles/LoadingSpinner.css";

// Componente para exibir feedback de carregamento
const LoadingSpinner = () => (
  <div className="loading-container">
    <div className="loading-spinner"></div>
    <p>Carregando...</p>
  </div>
);

// Componente de rota protegida
const PrivateRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  return isAuthenticated ? children : <Navigate to="/login" />;
};

// Componente de rota pública (redireciona se já estiver autenticado)
const PublicRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  return !isAuthenticated ? children : <Navigate to="/dashboard" />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Rota principal para a página Home */}
          <Route path="/" element={<Home />} />

          {/* Rotas de autenticação */}
          <Route
            path="/login"
            element={
              <PublicRoute>
                <Login />
              </PublicRoute>
            }
          />
          <Route
            path="/register"
            element={
              <PublicRoute>
                <Register />
              </PublicRoute>
            }
          />

          {/* Rota para dashboard/perfil do usuário (protegida) */}
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            }
          />

          {/* Rota para agendamento (protegida) */}
          <Route
            path="/agendar"
            element={
              <PrivateRoute>
                <div>Página de agendamento em construção</div>
              </PrivateRoute>
            }
          />

          {/* Rota para visualização de agendamentos (protegida) */}
          <Route
            path="/agendamentos/:id"
            element={
              <PrivateRoute>
                <div>Detalhes do agendamento em construção</div>
              </PrivateRoute>
            }
          />

          {/* Rota para cancelamento de agendamentos (protegida) */}
          <Route
            path="/agendamentos/:id/cancelar"
            element={
              <PrivateRoute>
                <div>Cancelamento de agendamento em construção</div>
              </PrivateRoute>
            }
          />

          {/* Rota para edição de perfil (protegida) */}
          <Route
            path="/perfil/editar"
            element={
              <PrivateRoute>
                <div>Edição de perfil em construção</div>
              </PrivateRoute>
            }
          />
          {/* Rota para página não encontrada */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
