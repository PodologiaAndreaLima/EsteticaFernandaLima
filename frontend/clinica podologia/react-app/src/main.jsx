import React from "react";
import ReactDOM from "react-dom/client";
import "./utils/processPolyfill.js"; // Adicionar polyfill para process
import "./styles/global.css";
import "./styles/fix-visibility.css";
import "./styles/fix-scrollbars.css";
import "./styles/fix-images.css"; // Correções de estilo para imagens
import "./config/env.js"; // Importar configuração de ambiente
import App from "./App.jsx";

console.log("Inicializando aplicação React com Vite...");

// Função para verificar se há problemas com o DOM
function checkDomReady() {
  return new Promise((resolve) => {
    // Se o documento já estiver pronto, resolva imediatamente
    if (
      document.readyState === "complete" ||
      document.readyState === "interactive"
    ) {
      setTimeout(resolve, 0);
      return;
    }

    // Caso contrário, espere pelo evento DOMContentLoaded
    document.addEventListener("DOMContentLoaded", resolve);
  });
}

// Inicializar a aplicação quando o DOM estiver pronto
checkDomReady().then(() => {
  // Configurar para indicar que o app está rodando
  window.APP_IS_RUNNING = true;

  // Garantir que o elemento raiz existe
  const rootElement = document.getElementById("root");
  if (rootElement) {
    try {
      ReactDOM.createRoot(rootElement).render(
        <React.StrictMode>
          <App />
        </React.StrictMode>
      );
      console.log("Aplicação React renderizada com sucesso!");
    } catch (error) {
      console.error("Erro ao renderizar a aplicação React:", error);
    }
  } else {
    console.error('Elemento raiz "root" não encontrado no DOM!');
  }
});
