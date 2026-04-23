// Configurações da API - Microserviço de Ordem de Serviço
// Separado da API principal para permitir configuração independente

const env = import.meta.env || {};

const API_CONFIG_MS = {
  // URL base para desenvolvimento local
  DEV_BASE_URL: env.VITE_API_MS_BASE_URL_DEV || "http://localhost:8081",

  // URL base para produção
  PROD_BASE_URL:
    env.VITE_API_MS_BASE_URL_PROD || "https://api-ms.clinicaestetica.com",

  // Timeout padrão das chamadas HTTP (microserviço pode ser mais lento)
  TIMEOUT: Number(env.VITE_API_MS_TIMEOUT_MS || 20000),

  // URLs por ambiente
  get BASE_URL() {
    const isProduction = import.meta.env.PROD;
    return isProduction ? this.PROD_BASE_URL : this.DEV_BASE_URL;
  },
};

export default API_CONFIG_MS;
