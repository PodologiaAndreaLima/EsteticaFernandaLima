import api from "./api"; 

export const ClienteService = {
  getClientes: async () => {
    try {
      const response = await api.get("/cliente");
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao buscar clientes"
      };
    }
  },

  criarCliente: async (dados) => {
    try {
      const response = await api.post("/cliente", dados);
      return {
        success: true,
        message: "Cliente criado com sucesso!"
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao criar cliente"
      };
    }
  },

  updateCliente: async (clienteId, dados) => {
    try {
      const response = await api.put(`/cliente/${clienteId}`, dados);
      return {
        success: true,
        data: response.data
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao atualizar cliente"
      };
    }
  },

  deleteCliente: async (clienteId) => {
    try {
      await api.delete(`/cliente/${clienteId}`);
      return {
        success: true,
        message: "Cliente deletado com sucesso"
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || "Erro ao deletar cliente"
      };
    }
  }
};