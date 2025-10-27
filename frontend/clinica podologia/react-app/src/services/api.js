import axios from "axios";
import API_CONFIG from "../config/apiConfig";

// Cria uma instância do axios com a URL base da API
const api = axios.create({
  baseURL: 'http://localhost:8080'
});

// Intercepta as requisições para adicionar o token de autenticação se disponível
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// Intercepta as respostas para tratar erros comuns
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Se o erro for 401 Unauthorized, pode significar que o token expirou
    if (error.response && error.response.status === 401) {
      // Limpa o token e redireciona para login
      localStorage.removeItem("token");
      localStorage.removeItem("user");

      // Se não estiver na página de login, redireciona
      if (!window.location.pathname.includes("/login")) {
        window.location.href = "/login?expired=true";
      }
    }

    return Promise.reject(error);
  }
);

export default api;
