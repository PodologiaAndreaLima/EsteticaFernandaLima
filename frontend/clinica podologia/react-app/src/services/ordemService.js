import api from "./api";
import API_CONFIG from "../config/apiConfig";

const path = API_CONFIG.ENDPOINTS?.SERVICE_ORDER || "/ordens-servico";

const serviceOrderBaseUrl =
  API_CONFIG.MICROSERVICES?.SERVICE_ORDER_BASE_URL?.trim() || "";

const extractErrorMessage = (errorPayload) => {
  if (!errorPayload) return "Erro inesperado";
  if (typeof errorPayload === "string") return errorPayload;

  if (Array.isArray(errorPayload)) {
    const mapped = errorPayload
      .map((item) => extractErrorMessage(item))
      .filter(Boolean);
    return mapped.join(" | ");
  }

  const directMessage =
    errorPayload.message ||
    errorPayload.error ||
    errorPayload.detail ||
    errorPayload.title ||
    errorPayload.path;

  if (typeof directMessage === "string" && directMessage.trim()) {
    return directMessage;
  }

  try {
    return JSON.stringify(errorPayload);
  } catch {
    return "Erro inesperado";
  }
};

const buildApiError = (err, fallback) => {
  const status = err?.response?.status;
  const apiMessage = extractErrorMessage(err?.response?.data);
  const message =
    apiMessage ||
    (typeof err?.message === "string" ? err.message : "") ||
    fallback;

  return status ? `HTTP ${status} - ${message}` : message;
};

const buildUrl = (id) => {
  const endpoint = `${path}${id ? `/${id}` : ""}`;
  if (!serviceOrderBaseUrl) return endpoint;
  const base = serviceOrderBaseUrl.replace(/\/+$/, "");
  const safeEndpoint = endpoint.startsWith("/") ? endpoint : `/${endpoint}`;
  return `${base}${safeEndpoint}`;
};

const toPageResponse = (payload) => {
  if (Array.isArray(payload)) {
    return {
      content: payload,
      page: 0,
      size: payload.length,
      totalElements: payload.length,
      totalPages: payload.length > 0 ? 1 : 0,
    };
  }

  if (payload && Array.isArray(payload.content)) {
    return {
      content: payload.content,
      page: Number(payload.page ?? 0),
      size: Number(payload.size ?? payload.content.length ?? 0),
      totalElements: Number(
        payload.totalElements ?? payload.content.length ?? 0,
      ),
      totalPages: Number(payload.totalPages ?? 0),
    };
  }

  return {
    content: [],
    page: 0,
    size: 0,
    totalElements: 0,
    totalPages: 0,
  };
};

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

      if (filters?.page !== undefined && filters?.page !== null) {
        params.page = filters.page;
      }

      if (filters?.size !== undefined && filters?.size !== null) {
        params.size = filters.size;
      }

      const resp = await api.get(buildUrl(), { params });
      // se a API devolver 204 (no content) axios chama como 204; tratamos
      if (resp.status === 204) {
        const pageResponse = toPageResponse([]);
        return {
          success: true,
          data: pageResponse.content,
          page: pageResponse,
        };
      }

      const pageResponse = toPageResponse(resp.data);
      return { success: true, data: pageResponse.content, page: pageResponse };
    } catch (err) {
      console.error("ordemService.list error", err);
      return {
        success: false,
        error: buildApiError(err, "Erro ao buscar ordens"),
      };
    }
  },

  async create(payload) {
    try {
      const resp = await api.post(buildUrl(), payload);
      return { success: true, data: resp.data };
    } catch (err) {
      console.error("ordemService.create error", err);
      return {
        success: false,
        error: buildApiError(err, "Erro ao criar ordem"),
      };
    }
  },

  async update(id, payload) {
    try {
      const resp = await api.put(buildUrl(id), payload);
      return { success: true, data: resp.data };
    } catch (err) {
      console.error("ordemService.update error", err);
      return {
        success: false,
        error: buildApiError(err, "Erro ao atualizar ordem"),
      };
    }
  },

  async remove(id) {
    try {
      const resp = await api.delete(buildUrl(id));
      // sucesso (204) => retornar success:true
      return { success: true };
    } catch (err) {
      console.error("ordemService.remove error", err);
      return {
        success: false,
        error: buildApiError(err, "Erro ao excluir ordem"),
      };
    }
  },
};
