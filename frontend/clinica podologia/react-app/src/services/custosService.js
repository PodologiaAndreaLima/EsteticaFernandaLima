import api from "./api";

const BASE = "/custos-extras";

/**
 * Converte "DD/MM/YYYY" ou "DD-MM-YYYY" para "YYYY-MM-DD".
 * Se a string já estiver em ISO ("YYYY-MM-DD") retorna ela validada.
 * Retorna null para valores vazios ou inválidos.
 */
const ddmmyyyyToIso = (s) => {
  if (s === undefined || s === null) return null;
  const str = String(s).trim();
  if (str === "") return null;

  const isoMatch = /^\d{4}-\d{2}-\d{2}$/.test(str);
  if (isoMatch) return str;

  // aceita DD/MM/YYYY ou DD-MM-YYYY
  const parts = str.includes("/") ? str.split("/") : str.split("-");
  if (parts.length !== 3) return null;

  let [a, b, c] = parts.map(p => p.trim());
  // detectar se usuário escreveu YYYY-MM-DD por engano em ordem invertida
  // se a tiver 4 dígitos, pode ser ano no começo -> já é ISO
  if (a.length === 4) {
    // a=yyyy, b=mm, c=dd -> reconstrói validação
    if (!/^\d{4}$/.test(a) || !/^\d{1,2}$/.test(b) || !/^\d{1,2}$/.test(c)) return null;
    const yyyy = a;
    const mm = b.padStart(2, "0");
    const dd = c.padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
  }

  // caso normal: a=dd, b=mm, c=yyyy
  if (!/^\d{1,2}$/.test(a) || !/^\d{1,2}$/.test(b) || !/^\d{2,4}$/.test(c)) return null;
  let dd = a.padStart(2, "0");
  let mm = b.padStart(2, "0");
  let yyyy = c.length === 2 ? `20${c}` : c;
  // validação simples de números
  if (Number(dd) < 1 || Number(dd) > 31) return null;
  if (Number(mm) < 1 || Number(mm) > 12) return null;
  if (!/^\d{4}$/.test(yyyy)) return null;

  return `${yyyy}-${mm}-${dd}`;
};

/**
 * Converte ISO para dd/mm/yyyy (para exibição no front)
 */
const isoToDdmmyyyy = (iso) => {
  if (!iso) return "";
  const parts = String(iso).split("-");
  if (parts.length !== 3) return "";
  return `${parts[2]}/${parts[1]}/${parts[0]}`;
};

export default {
  list: async () => {
    const res = await api.get(BASE);
    return (res.data || []).map(item => ({
      id: item.idCustoExtra ?? item.id,
      nome: item.nome,
      descricao: item.descricao,
      valor: item.valor ?? 0,
      data: item.data ? isoToDdmmyyyy(item.data) : "",
      isFixo: false,
    }));
  },

  create: async (formData) => {
    const payload = {
      nome: formData.nome,
      descricao: formData.descricao,
      valor: parseFloat(String(formData.valor).replace(",", ".")) || 0,
      data: ddmmyyyyToIso(formData.data),
    };
    // console.log("custosService.create payload:", payload);
    const res = await api.post(BASE, payload);
    return res.data;
  },

  update: async (id, formData) => {
    const payload = {
      nome: formData.nome,
      descricao: formData.descricao,
      valor: parseFloat(String(formData.valor).replace(",", ".")) || 0,
      data: ddmmyyyyToIso(formData.data),
    };
    // console.log("custosService.update payload:", payload);
    const res = await api.put(`${BASE}/${id}`, payload);
    return res.data;
  },

  remove: async (id) => {
    const res = await api.delete(`${BASE}/${id}`);
    return res.data;
  },
};
