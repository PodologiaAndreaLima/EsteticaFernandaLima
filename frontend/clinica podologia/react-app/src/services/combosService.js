import api from "./api";

const BASE = "/combo";

export default {
  list: async () => {
    const res = await api.get(BASE);
    return res.data;
  },

  getById: async (id) => {
    const res = await api.get(`${BASE}/${id}`);
    return res.data;
  },

  create: async (payload) => {
    // payload: { nome, descricao, valorFinal } where valorFinal is number
    const res = await api.post(BASE, payload);
    return res.data;
  },

  update: async (id, payload) => {
    const res = await api.put(`${BASE}/${id}`, payload);
    return res.data;
  },

  remove: async (id) => {
    const res = await api.delete(`${BASE}/${id}`);
    return res.data;
  },
};
