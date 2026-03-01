import api from "./api";

const path = "/ordem-servico";

export default {
  async list(filters = {}) {
    try {
      const params = {};

      if (filters?.data) {
        params.data = filters.data;
      }

      if (filters?.dataInicio) {
        params.dataInicio = filters.dataInicio;
      }

      if (filters?.dataFim) {
        params.dataFim = filters.dataFim;
      }

      if (filters?.usuarioId) {
        params.usuarioId = filters.usuarioId;
      }

      const resp = await api.get(path, { params });
      // se a API devolver 204 (no content) axios chama como 204; tratamos
      if (resp.status === 204) return { success: true, data: [] };
      return { success: true, data: resp.data || [] };
    } catch (err) {
      console.error("ordemService.list error", err);
      return {
        success: false,
        error: err.response?.data || err.message || "Erro ao buscar ordens",
      };
    }
  },

  async create(payload) {
    try {
      const resp = await api.post(path, payload);
      return { success: true, data: resp.data };
    } catch (err) {
      console.error("ordemService.create error", err);
      return {
        success: false,
        error: err.response?.data || err.message || "Erro ao criar ordem",
      };
    }
  },

  async update(id, payload) {
    try {
      const resp = await api.put(`${path}/${id}`, payload);
      return { success: true, data: resp.data };
    } catch (err) {
      console.error("ordemService.update error", err);
      return {
        success: false,
        error: err.response?.data || err.message || "Erro ao atualizar ordem",
      };
    }
  },

  async remove(id) {
    try {
      const resp = await api.delete(`${path}/${id}`);
      // sucesso (204) => retornar success:true
      return { success: true };
    } catch (err) {
      console.error("ordemService.remove error", err);
      return {
        success: false,
        error: err.response?.data || err.message || "Erro ao excluir ordem",
      };
    }
  },
};
