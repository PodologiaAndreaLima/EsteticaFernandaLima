import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";
import fs from "fs";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react({
      // Opções adicionais para o plugin React
      jsxRuntime: "automatic",
      babel: {
        plugins: [
          // Adiciona plugins Babel se necessário
        ],
      },
    }),
  ],
  resolve: {
    alias: {
      // Define aliases para manter compatibilidade com CRA
      "@": path.resolve(__dirname, "./src"),
      src: path.resolve(__dirname, "./src"),
      components: path.resolve(__dirname, "./src/components"),
      pages: path.resolve(__dirname, "./src/pages"),
      assets: path.resolve(__dirname, "./src/assets"),
      styles: path.resolve(__dirname, "./src/styles"),
      utils: path.resolve(__dirname, "./src/utils"),
      contexts: path.resolve(__dirname, "./src/contexts"),
      services: path.resolve(__dirname, "./src/services"),
      config: path.resolve(__dirname, "./src/config"),
    },
    extensions: [".js", ".jsx", ".json", ".css"],
  },
  server: {
    port: 3000,
    open: true,
    hmr: {
      overlay: true,
    },
    watch: {
      usePolling: true,
    },
  },
  // Configuração para lidar com variáveis de ambiente como no CRA
  define: {
    "process.env": {
      NODE_ENV: JSON.stringify(process.env.NODE_ENV || "development"),
      PUBLIC_URL: "/",
    },
  },
  publicDir: "public",
  base: "/",
  css: {
    // Opções para processamento de CSS
    modules: {
      localsConvention: "camelCase",
    },
  },
  build: {
    outDir: "dist",
    assetsDir: "assets",
    sourcemap: true,
    minify: "terser",
  },
});
