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
        if (token) {
          // Verifica se é o admin simulado
          if (
            token === "admin-token-simulated" ||
            token === "funcionario-token-simulated"
          ) {
            // Para o admin simulado, pegamos os dados diretamente do localStorage
            const userData = JSON.parse(localStorage.getItem("user"));
            if (userData) {
              setUser(userData);
            }
          } else {
            // Para tokens normais, tenta validar no servidor
            try {
              const isValid = await AuthService.validateToken();

              if (isValid) {
                // Se o token for válido, busca os dados do usuário
                const userResponse = await AuthService.getCurrentUser();
                if (userResponse.success) {
                  setUser(userResponse.user);
                  localStorage.setItem(
                    "user",
                    JSON.stringify(userResponse.user)
                  );
                } else {
                  // Se não conseguir buscar os dados, faz logout
                  AuthService.logout();
                }
              } else {
                // Se o token não for válido, faz logout
                AuthService.logout();
              }
            } catch (error) {
              console.log("Erro ao validar token:", error);
              // Em caso de erro, tentamos usar os dados do localStorage
              const userData = JSON.parse(localStorage.getItem("user"));
              if (userData) {
                setUser(userData);
              } else {
                AuthService.logout();
              }
            }
          }
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

      if (result.success) {
        setUser(result.user);
        console.log("Usuário setado no contexto:", result.user);
        return result; // Retorna { success: true, user: {...}, token: "..." }
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
