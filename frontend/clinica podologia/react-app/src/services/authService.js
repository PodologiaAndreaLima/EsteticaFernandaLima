import api from "./api";
import API_CONFIG from "../config/apiConfig";
import Cliente from "../models/Cliente";
import Funcionario from "../models/Funcionario";

export const AuthService = {
  // Função para fazer login
  login: async (identifier, senha) => {
    try {
      const response = await api.post("/usuarios/login", {
        email: identifier,
        senha: senha
      });

      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        localStorage.setItem("user", JSON.stringify({
          id: response.data.userId,
          nome: response.data.nomeCompleto,
          email: response.data.email,
          role: response.data.role
        }));
      }

      return {
        success: true,
        user: response.data,
        token: response.data.token
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Credenciais inválidas"
      };
    }
  },

  // Função para registrar novo funcionário
  register: async (dados) => {
    try {
      const response = await api.post("/usuarios", {
        nomeCompleto: dados.nomeCompleto,
        email: dados.email,
        senha: dados.senha,
        cpf: dados.cpf,
        telefone: dados.telefone,
        servicosPrestados: dados.servicosPrestados,
        bio: dados.bio,
        role: dados.role
      });

      return {
        success: true,
        message: "Usuário cadastrado com sucesso!"
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao cadastrar usuário"
      };
    }
  },

  // Buscar todos os funcionários
  getUsuarios: async () => {
    try {
      const response = await api.get("/usuarios");
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao buscar funcionários"
      };
    }
  },

  // Atualizar funcionário
  updateUsuario: async (usuarioId, dados) => {
    try {
      const response = await api.put(`/usuarios/${usuarioId}`, dados);
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao atualizar funcionário"
      };
    }
  },

  // Deletar funcionário
  deleteUsuario: async (usuarioId) => {
    try {
      await api.delete(`/usuarios/${usuarioId}`);
      return {
        success: true,
        message: "Funcionário deletado com sucesso"
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao deletar funcionário"
      };
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
