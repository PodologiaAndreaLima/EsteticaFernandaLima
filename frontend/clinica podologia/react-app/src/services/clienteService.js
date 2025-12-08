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

// Wrapper para compatibilidade com o padrão usado em outros services
export default {
  async list() {
    try {
      const result = await ClienteService.getClientes();
      if (result.success) {
        return { success: true, data: result.data || [] };
      }
      return { success: false, data: [] };
    } catch (err) {
      console.error("clienteService.list error", err);
      return { success: false, data: [] };
    }
  },

  async listAll() {
    return this.list();
  },

  async getById(id) {
    try {
      const result = await api.get(`/cliente/${id}`);
      return { success: true, data: result.data };
    } catch (err) {
      console.error("clienteService.getById error", err);
      throw err;
    }
  },

  async create(data) {
    try {
      const result = await ClienteService.criarCliente(data);
      return result;
    } catch (err) {
      console.error("clienteService.create error", err);
      throw err;
    }
  },

  async update(id, data) {
    try {
      const result = await ClienteService.updateCliente(id, data);
      return result;
    } catch (err) {
      console.error("clienteService.update error", err);
      throw err;
    }
  },

  async remove(id) {
    try {
      const result = await ClienteService.deleteCliente(id);
      return result;
    } catch (err) {
      console.error("clienteService.remove error", err);
      throw err;
    }
  },

  async delete(id) {
    return this.remove(id);
  }
};