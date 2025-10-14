import api from "./api";
import API_CONFIG from "../config/apiConfig";

export const UserService = {
  // Buscar todos os usuários (acesso admin)
  getAllUsers: async () => {
    try {
      const response = await api.get(API_CONFIG.ENDPOINTS.USERS.BASE);
      return {
        success: true,
        users: response.data,
      };
    } catch (error) {
      console.error("Erro ao buscar usuários:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao buscar usuários",
      };
    }
  },

  // Buscar usuário por ID
  getUserById: async (userId) => {
    try {
      const response = await api.get(
        `${API_CONFIG.ENDPOINTS.USERS.BASE}/${userId}`
      );
      return {
        success: true,
        user: response.data,
      };
    } catch (error) {
      console.error("Erro ao buscar usuário:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao buscar usuário",
      };
    }
  },

  // Criar novo usuário (pode ser diferente do registro, usado por administradores)
  createUser: async (userData) => {
    try {
      const response = await api.post(
        API_CONFIG.ENDPOINTS.USERS.BASE,
        userData
      );
      return {
        success: true,
        user: response.data,
      };
    } catch (error) {
      console.error("Erro ao criar usuário:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao criar usuário",
      };
    }
  },

  // Atualizar usuário existente
  updateUser: async (userId, userData) => {
    try {
      const response = await api.put(
        `${API_CONFIG.ENDPOINTS.USERS.BASE}/${userId}`,
        userData
      );
      return {
        success: true,
        user: response.data,
      };
    } catch (error) {
      console.error("Erro ao atualizar usuário:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao atualizar usuário",
      };
    }
  },

  // Excluir usuário
  deleteUser: async (userId) => {
    try {
      await api.delete(`${API_CONFIG.ENDPOINTS.USERS.BASE}/${userId}`);
      return {
        success: true,
      };
    } catch (error) {
      console.error("Erro ao excluir usuário:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao excluir usuário",
      };
    }
  },

  // Alterar senha do usuário
  changePassword: async (userId, passwordData) => {
    try {
      await api.put(
        `${API_CONFIG.ENDPOINTS.USERS.BASE}/${userId}/password`,
        passwordData
      );
      return {
        success: true,
      };
    } catch (error) {
      console.error("Erro ao alterar senha:", error);
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao alterar senha",
      };
    }
  },
};
