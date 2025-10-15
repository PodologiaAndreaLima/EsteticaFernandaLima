// src/utils/assetUtils.js
// Utilitário para facilitar o carregamento de recursos estáticos no Vite

/**
 * Função para importar recursos estáticos dinamicamente no Vite
 * @param {String} path - Caminho relativo do recurso na pasta public
 * @returns {String} URL para o recurso
 */
export function getAssetPath(path) {
  // Remove a barra inicial se existir
  if (path.startsWith("/")) {
    path = path.substring(1);
  }

  return new URL(`../assets/${path}`, import.meta.url).href;
}

/**
 * Função para obter o caminho de imagens
 * @param {String} name - Nome do arquivo de imagem
 * @returns {String} URL para a imagem
 */
export function getImagePath(name) {
  return getAssetPath(`images/${name}`);
}

/**
 * Função para obter o caminho de vídeos
 * @param {String} name - Nome do arquivo de vídeo
 * @returns {String} URL para o vídeo
 */
export function getVideoPath(name) {
  return getAssetPath(`videos/${name}`);
}

/**
 * Função para obter o caminho de ícones
 * @param {String} name - Nome do arquivo de ícone
 * @returns {String} URL para o ícone
 */
export function getIconPath(name) {
  return getAssetPath(`icons/${name}`);
}
