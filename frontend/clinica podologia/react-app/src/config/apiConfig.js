// Configurações da API - Clínica de Estética
const API_CONFIG = {
  // URL base para desenvolvimento local
  DEV_BASE_URL: "http://localhost:8080",

  // URL base para produção
  PROD_BASE_URL: "https://api.clinicaestetica.com",

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
      BASE: "/cliente", // Mantido para cliente
      CURRENT: "/cliente/me", // Para cliente
      PASSWORD: "/cliente/:id/senha", // Endpoint para atualização de senha
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
  },
};

// Determina qual URL base usar com base no ambiente
const isProduction = process.env.NODE_ENV === "production";
API_CONFIG.BASE_URL = isProduction
  ? API_CONFIG.PROD_BASE_URL
  : API_CONFIG.DEV_BASE_URL;

export default API_CONFIG;
