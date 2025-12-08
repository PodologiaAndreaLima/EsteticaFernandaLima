import api from "./api";

const BASE = "/servico-produto";

const toBackendPayload = (formData) => {
  const valorCustoRaw = formData.valorCusto ?? formData.despesa ?? formData.valorCompra ?? "";
  const valorVendaRaw = formData.valorVenda ?? "";

  const despesa = valorCustoRaw !== undefined && valorCustoRaw !== ""
    ? parseFloat(String(valorCustoRaw).replace(",", "."))
    : 0;

  const valorVenda = valorVendaRaw !== undefined && valorVendaRaw !== ""
    ? parseFloat(String(valorVendaRaw).replace(",", "."))
    : 0;

  // Normaliza isProduto aceitando boolean, "true"/"false", 1/0
  const raw = formData.isProduto;
  const isProdutoNormalized = raw === true || raw === "true" || raw === 1 || raw === "1";

  const payload = {
    nome: formData.nome,
    despesa,
    valorVenda,
    descricao: formData.descricao || "",
  };

  if (formData.marca !== undefined) payload.marca = formData.marca;
  if (formData.categoria !== undefined) payload.categoria = formData.categoria;

  // envia ambos (camelCase e snake_case) para compatibilidade
  if (isProdutoNormalized !== undefined) {
    payload.isProduto = isProdutoNormalized;
    payload.is_produto = isProdutoNormalized ? 1 : 0;
  }

  return payload;
};

export default {
  list: async () => {
    const res = await api.get(BASE);
    // Filtrar apenas serviços (isProduto = false, null ou undefined)
    const allItems = res.data || [];
    const servicos = allItems.filter(item => {
      // Verifica se é produto em qualquer variação do campo
      const eProduto = item.isProduto === true || item.produto === true || item.is_produto === 1;
      return !eProduto; // Retorna true se NÃO for produto (ou seja, é serviço)
    });
    console.log("DEBUG servicoProdutoService.list - Total:", allItems.length, "Serviços:", servicos.length);
    return servicos;
  },

  // alias caso algum componente ainda chame listAll
  listAll: async () => {
    const res = await api.get(BASE);
    return res.data; // Retorna tudo
  },

  getById: async (id) => {
    const res = await api.get(`${BASE}/${id}`);
    return res.data;
  },

  create: async (formData) => {
    const payload = toBackendPayload(formData);
    const res = await api.post(BASE, payload);
    return res.data;
  },

  update: async (id, formData) => {
    const payload = toBackendPayload(formData);
    const res = await api.put(`${BASE}/${id}`, payload);
    return res.data;
  },

  remove: async (id) => {
    const res = await api.delete(`${BASE}/${id}`);
    return res.data;
  }
};
