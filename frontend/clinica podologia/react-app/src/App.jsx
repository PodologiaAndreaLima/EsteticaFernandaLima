import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Login from "./pages/sistema/Login";
import Register from "./pages/sistema/Register";
import Home from "./pages/site/Home";
import Dashboard from "./pages/sistema/Dashboard";
import PaginaInicial from "./pages/sistema/PaginaInicial";
import Clientes from "./pages/sistema/Clientes";
import Funcionarios from "./pages/sistema/Funcionarios";
import SistemaLayout from "./pages/sistema/SistemaLayout";
import Servicos from "./pages/sistema/Servicos";
import Produtos from "./pages/sistema/Produtos";
import Perfil from "./pages/sistema/Perfil";
import OrdemServico from "./pages/sistema/OrdemServico";
import Custos from "./pages/sistema/Custos";
import Combos from "./pages/sistema/Combos";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import "./styles/LoadingSpinner.css";
import "./styles/fix-system-overflow.css";

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

  return !isAuthenticated ? children : <Navigate to="/sistema" />;
};

const ProtectedRoute = ({ children, requiredRoles = [] }) => {
  const { isAuthenticated, userRole, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (!userRole || !requiredRoles.includes(userRole)) {
    return <Navigate to="/sistema" />;
  }

  return children;
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

          {/* Rota especial para acesso administrativo (não exposta no menu) */}
          <Route
            path="/admin-login"
            element={
              <PublicRoute>
                <Login />
              </PublicRoute>
            }
          />

          {/* Rotas do sistema interno - temporariamente sem proteção para visualização */}
          <Route
            path="/sistema"
            element={
              <PrivateRoute>
                <SistemaLayout />
              </PrivateRoute>
            }
          >
            {/* Página inicial do sistema */}
            <Route index element={<PaginaInicial />} />

            {/* Páginas do sistema */}
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="ordem-servico" element={<OrdemServico />} />
            <Route path="clientes" element={<Clientes />} />
            <Route path="servicos" element={<Servicos />} />
            <Route path="produtos" element={<Produtos />} />
            <Route path="combos" element={<Combos />} />
            <Route
              path="funcionarios"
              element={
                <ProtectedRoute requiredRoles={["ADMIN"]}>
                  <Funcionarios />
                </ProtectedRoute>
              }
            />
            <Route
              path="custos"
              element={
                <ProtectedRoute requiredRoles={["ADMIN"]}>
                  <Custos />
                </ProtectedRoute>
              }
            />
            <Route path="perfil" element={<Perfil />} />
          </Route>

          {/* Rota para página não encontrada */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
