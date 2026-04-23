import axios from "axios";
import API_CONFIG_MS from "../config/apiConfigMs";

// Cria uma instância do axios com a URL base do microserviço
const apiMs = axios.create({
  baseURL: API_CONFIG_MS.BASE_URL,
  timeout: API_CONFIG_MS.TIMEOUT,
});

// Intercepta as requisições para adicionar o token de autenticação se disponível
apiMs.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // Garantir que o Content-Type seja application/json para POST e PUT
    if (
      (config.method === "post" || config.method === "put") &&
      !config.headers["Content-Type"]
    ) {
      config.headers["Content-Type"] = "application/json";
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepta as respostas para tratar erros comuns
apiMs.interceptors.response.use(
  (response) => response,
  (error) => {
    // Se o erro for 401 Unauthorized, pode significar que o token expirou
    if (error.response && error.response.status === 401) {
      // Limpa o token e redireciona para login
      sessionStorage.removeItem("token");
      sessionStorage.removeItem("user");

      // Se não estiver na página de login, redireciona
      if (!window.location.pathname.includes("/login")) {
        window.location.href = "/login?expired=true";
      }
    }

    return Promise.reject(error);
  }
);

export default apiMs;
