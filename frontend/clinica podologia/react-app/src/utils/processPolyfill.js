/* eslint-disable */
// Para compatibilidade com processos CRA
const process = window.process || {};
process.env = process.env || {};
process.env.NODE_ENV = process.env.NODE_ENV || "development";
process.env.PUBLIC_URL = import.meta.env.BASE_URL || "";

// Para componentes que podem estar procurando essas variáveis
window.process = process;

// Fornecer outras variáveis compatíveis com CRA
window.__DEV__ = import.meta.env.DEV;

// Polyfill para eventuais importações dinâmicas
if (typeof window.require === "undefined") {
  window.require = function (module) {
    console.warn(`Tentativa de importação CommonJS: ${module}`);
    return null;
  };
}

export default process;
