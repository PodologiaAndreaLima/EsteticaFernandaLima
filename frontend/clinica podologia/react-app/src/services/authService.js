import api from "./api";
import API_CONFIG from "../config/apiConfig";
import Cliente from "../models/Cliente";
import Funcionario from "../models/Funcionario";

export const AuthService = {
  // Função para fazer login
  login: async (identifier, senha) => {
    try {
      // Para o login, precisamos implementar um endpoint específico no backend
      // Por enquanto, estamos adaptando para a implementação atual
      // O ideal seria ter um endpoint /auth/login no backend

      // Agora usamos apenas email para login de funcionários
      const loginData = {
        email: identifier,
        senha,
        isStaff: true, // Indica que é login de funcionário
      };

      // Fazemos a chamada para o backend
      const response = await api.post(
        API_CONFIG.ENDPOINTS.AUTH.LOGIN,
        loginData
      );

      // Armazena o token no localStorage
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        // Salva os dados do funcionário se disponíveis
        if (response.data.funcionario) {
          const funcionario = Funcionario.fromAPI(response.data.funcionario);
          localStorage.setItem("user", JSON.stringify(funcionario));
        }
      }

      return {
        success: true,
        user: response.data.funcionario
          ? Funcionario.fromAPI(response.data.funcionario)
          : null,
        token: response.data.token,
      };
    } catch (error) {
      console.error("Erro no login:", error);
      const errorMessage =
        error.response?.data?.message || "Erro ao fazer login";
      return { success: false, error: errorMessage };
    }
  },

  // Função para registrar novo funcionário
  register: async (userData) => {
    try {
      // Convertemos os dados para o formato esperado pelo backend
      const funcionarioData = new Funcionario(userData).toAPI();

      // Fazemos a chamada para registrar o funcionário
      const response = await api.post(
        API_CONFIG.ENDPOINTS.AUTH.REGISTER_STAFF,
        funcionarioData
      );

      // No caso do backend Spring Boot atual, não retorna token após registro
      // Precisamos implementar esse comportamento no backend ou fazer login após registro
      const funcionarioRegistrado = response.data;

      // Como não temos token automático após registro, apenas retornamos a mensagem de sucesso
      // Funcionários precisam ser aprovados antes de fazer login
      if (funcionarioRegistrado) {
        return {
          success: true,
          // Não salvamos o usuário no localStorage neste caso
          message:
            "Solicitação enviada com sucesso! Aguarde a aprovação da administração para acessar o sistema.",
        };
      }

      return {
        success: true,
        message: "Solicitação enviada com sucesso! Aguarde aprovação.",
      };
    } catch (error) {
      console.error("Erro no registro:", error);
      const errorMessage =
        error.response?.data?.message || "Erro ao registrar usuário";
      return { success: false, error: errorMessage };
    }
  },

  // Função para obter os dados do funcionário logado
  // Como o backend não tem um endpoint /funcionario/me, usamos o ID armazenado localmente
  getCurrentUser: async () => {
    try {
      // Obtém o usuário do localStorage
      const localUser = AuthService.getLocalUser();

      if (!localUser || !localUser.id) {
        return { success: false, error: "Funcionário não encontrado" };
      }

      // Busca os dados atualizados do funcionário
      const response = await api.get(
        `${API_CONFIG.ENDPOINTS.STAFF.BASE}/${localUser.id}`
      );

      if (response.data) {
        // Atualiza o localStorage com os dados mais recentes
        const funcionario = Funcionario.fromAPI(response.data);
        localStorage.setItem("user", JSON.stringify(funcionario));

        return {
          success: true,
          user: funcionario,
        };
      }

      return { success: false, error: "Erro ao buscar dados do usuário" };
    } catch (error) {
      console.error("Erro ao buscar usuário:", error);
      return { success: false, error: "Erro ao buscar dados do usuário" };
    }
  },

  // Função para atualizar os dados do funcionário
  updateUser: async (userId, userData) => {
    try {
      // Convertemos para o formato esperado pelo backend
      const funcionarioData = new Funcionario(userData).toAPI();

      // Fazemos a chamada para atualizar o funcionário
      const response = await api.put(
        `${API_CONFIG.ENDPOINTS.STAFF.BASE}/${userId}`,
        funcionarioData
      );

      if (response.data) {
        // Atualiza o localStorage com os dados atualizados
        const funcionarioAtualizado = Funcionario.fromAPI(response.data);
        localStorage.setItem("user", JSON.stringify(funcionarioAtualizado));

        return {
          success: true,
          user: funcionarioAtualizado,
        };
      }

      return { success: false, error: "Erro ao atualizar dados" };
    } catch (error) {
      console.error("Erro ao atualizar usuário:", error);
      const errorMessage =
        error.response?.data?.message || "Erro ao atualizar dados";
      return { success: false, error: errorMessage };
    }
  },

  // Função para verificar se o token é válido
  validateToken: async () => {
    try {
      await api.get(API_CONFIG.ENDPOINTS.AUTH.VALIDATE);
      return true;
    } catch (error) {
      return false;
    }
  },

  // Função para fazer logout
  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },

  // Função para obter o usuário atual do localStorage
  getLocalUser: () => {
    const user = localStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  },
};
