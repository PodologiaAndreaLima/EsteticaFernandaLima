import api from "./api";

const BASE = "/custos-fixos";

export default {
  list: async () => {
    const res = await api.get(BASE);
    return res.data || [];
  },

  create: async (formData) => {
    const payload = {
      nome: formData.nome,
      descricao: formData.descricao,
      valorMensal: parseFloat(String(formData.valorMensal ?? formData.valor).replace(",", ".")) || 0,
    };
    const res = await api.post(BASE, payload);
    return res.data;
  },

  update: async (id, formData) => {
    const payload = {
      nome: formData.nome,
      descricao: formData.descricao,
      valorMensal: parseFloat(String(formData.valorMensal ?? formData.valor).replace(",", ".")) || 0,
    };
    const res = await api.put(`${BASE}/${id}`, payload);
    return res.data;
  },

  remove: async (id) => {
    const res = await api.delete(`${BASE}/${id}`);
    return res.data;
  },
};
