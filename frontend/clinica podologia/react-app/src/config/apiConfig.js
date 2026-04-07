// Configurações da API - Clínica de Estética
const env = import.meta.env || {};

const toPath = (value, fallback) => {
  if (!value || typeof value !== "string") return fallback;
  return value.startsWith("/") ? value : `/${value}`;
};

const API_CONFIG = {
  // URL base para desenvolvimento local
  DEV_BASE_URL: env.VITE_API_BASE_URL_DEV || "http://localhost:8080",

  // URL base para produção
  PROD_BASE_URL:
    env.VITE_API_BASE_URL_PROD || "https://api.clinicaestetica.com",

  // Timeout padrão das chamadas HTTP
  TIMEOUT: Number(env.VITE_API_TIMEOUT_MS || 15000),

  // Endpoints comuns
  ENDPOINTS: {
    AUTH: {
      LOGIN: "/auth/login",
      REGISTER: "/cliente", // Manter para compatibilidade
      REGISTER_STAFF: "/funcionario", // Nova rota para registro de funcionários
      VALIDATE: "/auth/validate",
      REFRESH: "/auth/refresh",
    },
    USERS: {
      BASE: "/usuarios", // Endpoint para usuários/funcionários
      CURRENT: "/usuarios/me",
      PASSWORD: "/usuarios/:id/senha", // Endpoint para atualização de senha
    },
    STAFF: {
      BASE: "/funcionario", // Novo endpoint para funcionários
      CURRENT: "/funcionario/me", // Para funcionários
      PASSWORD: "/funcionario/:id/senha", // Endpoint para atualização de senha
    },
    APPOINTMENTS: {
      BASE: "/agendamento", // A ser implementado no backend
      USER: "/agendamento/cliente/:id", // A ser implementado no backend
    },
    SERVICE_ORDER: toPath(env.VITE_ORDEM_SERVICO_ENDPOINT, "/ordens-servico"),
  },
  MICROSERVICES: {
    SERVICE_ORDER_BASE_URL: env.VITE_ORDEM_SERVICO_BASE_URL || "",
  },
};

// Determina qual URL base usar com base no ambiente
const isProduction = Boolean(env.PROD) || process.env.NODE_ENV === "production";
API_CONFIG.BASE_URL =
  env.VITE_API_BASE_URL ||
  (isProduction ? API_CONFIG.PROD_BASE_URL : API_CONFIG.DEV_BASE_URL);

export default API_CONFIG;
