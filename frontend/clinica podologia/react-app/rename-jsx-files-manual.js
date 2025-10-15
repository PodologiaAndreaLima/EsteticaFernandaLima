// rename-jsx-files-manual.js
const fs = require("fs");
const path = require("path");

// Lista de arquivos para renomear
const filesToRename = [
  // Componentes
  "src/components/Beneficios.js",
  "src/components/Banner.js",
  "src/components/Certificacoes.js",
  "src/components/Contato.js",
  "src/components/EventosCarrossel.js",
  "src/components/Footer.js",
  "src/components/Header.js",
  "src/components/MVV.js",
  "src/components/QuemSomos.js",
  "src/components/Relatos.js",
  "src/components/Servicos.js",
  "src/components/SobreNos.js",
  // Páginas
  "src/pages/Auth.js",
  "src/pages/Dashboard.js",
  "src/pages/Home.js",
  "src/pages/HomeNoAnimation.js",
  "src/pages/Login.js",
  "src/pages/Register.js",
  // Contexts
  "src/contexts/AuthContext.js",
  // App principal
  "src/App.js",
  "src/index.js",
];

// Função para renomear arquivo
function renameFile(filePath) {
  try {
    const fullPath = path.resolve(__dirname, filePath);

    if (fs.existsSync(fullPath)) {
      const newPath = fullPath.replace(".js", ".jsx");
      const content = fs.readFileSync(fullPath, "utf8");

      // Primeiro cria o novo arquivo
      fs.writeFileSync(newPath, content, "utf8");

      // Depois remove o arquivo antigo
      fs.unlinkSync(fullPath);

      console.log(`Renomeado: ${filePath} -> ${path.basename(newPath)}`);
    } else {
      console.log(`Arquivo não encontrado: ${filePath}`);
    }
  } catch (error) {
    console.error(`Erro ao processar ${filePath}:`, error);
  }
}

// Renomear cada arquivo da lista
filesToRename.forEach((file) => {
  renameFile(file);
});

console.log(
  "\nProcesso concluído! Verifique se todos os arquivos foram renomeados corretamente."
);
