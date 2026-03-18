import React, { createContext, useState, useContext, useEffect } from "react";
import { AuthService } from "../services/authService";

// Criar o contexto de autenticação
const AuthContext = createContext();

// Provedor do contexto
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Verificar se o usuário já está autenticado ao inicializar
  useEffect(() => {
    const checkAuth = async () => {
      setLoading(true);
      try {
        // Verifica se há um token armazenado
        const token = localStorage.getItem("token");
        const storedUser = localStorage.getItem("user");

        if (token && storedUser) {
          // Valida minimamente: verifica se o token JWT não está expirado
          // antes de restaurar a sessão do localStorage
          try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            if (payload.exp * 1000 < Date.now()) {
              AuthService.logout();
              setLoading(false);
              return;
            }
          } catch {
            AuthService.logout();
            setLoading(false);
            return;
          }
          // Se tem token válido e dados do usuário no localStorage, apenas usa
          const userData = JSON.parse(storedUser);
          setUser(userData);
          setLoading(false);
          return;
        }

        // Se não tem dados, faz logout
        if (!token || !storedUser) {
          AuthService.logout();
        }
      } catch (error) {
        console.error("Erro ao verificar autenticação:", error);
        AuthService.logout();
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  // Função de login usando o serviço de autenticação
  const login = async (identifier, password) => {
    try {
      const result = await AuthService.login(identifier, password);

      if (result.success && result.user) {
        setUser({
          id: result.user.userId,
          nome: result.user.nomeCompleto,
          email: result.user.email,
          role: result.user.role
        });
        console.log("Usuário setado no contexto:", result.user);
        return result;
      }

      return result;
    } catch (error) {
      console.error("Erro ao fazer login:", error);
      return { success: false, error: "Ocorreu um erro durante o login" };
    }
  };

  // Função de registro usando o serviço de autenticação
  const register = async (userData) => {
    try {
      const result = await AuthService.register(userData);

      if (result.success) {
        setUser(result.user);
        localStorage.setItem("user", JSON.stringify(result.user));
      }

      return result;
    } catch (error) {
      console.error("Erro ao registrar:", error);
      return { success: false, error: "Ocorreu um erro durante o registro" };
    }
  };

  // Função de logout usando o serviço de autenticação
  const logout = () => {
    // Remover token e dados do usuário do localStorage
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    // Limpar o estado do usuário
    setUser(null);
  };

  // Valores a serem disponibilizados pelo contexto
  const value = {
    user,
    loading,
    login,
    register,
    logout,
    isAuthenticated: !!user,
    userRole: user?.role,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Hook personalizado para usar o contexto
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth deve ser usado dentro de um AuthProvider");
  }
  return context;
};
