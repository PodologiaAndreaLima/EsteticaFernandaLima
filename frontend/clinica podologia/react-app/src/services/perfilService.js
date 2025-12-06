import api from "./api";
const BASE = "/usuarios";

export default {
  getById: async (id) => {
    const res = await api.get(`${BASE}/${id}`);
    return res.data;
  },
  update: async (id, payload) => {
    const res = await api.put(`${BASE}/${id}`, payload);
    return res.data;
  },
  changePassword: async (id, payload) => {
    const res = await api.post(`${BASE}/${id}/alterar-senha`, payload);
    return res.data;
  },
};
