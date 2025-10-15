// rename-jsx-files.js
const fs = require("fs");
const path = require("path");

// Diretórios para verificar arquivos JS com JSX
const directories = [
  path.resolve(__dirname, "src/components"),
  path.resolve(__dirname, "src/pages"),
  path.resolve(__dirname, "src/contexts"),
  path.resolve(__dirname, "src"),
];

// Padrões que indicam que um arquivo provavelmente contém JSX
const jsxPatterns = [
  /<[a-zA-Z]+[^>]*>/, // Qualquer tag HTML/JSX
  /className=/, // className é um atributo específico de React
  /import React/, // Importação do React
  /<\/[a-zA-Z]+>/, // Tag de fechamento
  /React\.createElement/,
];

// Arquivos que devem ser ignorados
const ignoreFiles = ["reportWebVitals.js", "setupTests.js", "serviceWorker.js"];

// Armazena arquivos renomeados para atualizar importações
const renamedFiles = {};
// Armazena arquivos para serem atualizados
const filesToUpdate = [];

// Função para verificar se um arquivo contém JSX
function containsJSX(filePath) {
  try {
    const content = fs.readFileSync(filePath, "utf8");
    return jsxPatterns.some((pattern) => pattern.test(content));
  } catch (error) {
    console.error(`Erro ao ler o arquivo ${filePath}:`, error);
    return false;
  }
}

// Função recursiva para encontrar e processar arquivos
function processDirectory(directory) {
  const entries = fs.readdirSync(directory, { withFileTypes: true });

  entries.forEach((entry) => {
    const entryPath = path.join(directory, entry.name);

    if (entry.isDirectory()) {
      // Pula node_modules e outros diretórios ignorados
      if (entry.name !== "node_modules" && !entry.name.startsWith(".")) {
        processDirectory(entryPath);
      }
    } else if (
      entry.name.endsWith(".js") &&
      !ignoreFiles.includes(entry.name)
    ) {
      if (containsJSX(entryPath)) {
        const newPath = entryPath.replace(".js", ".jsx");
        // Armazena para renomear depois
        filesToUpdate.push({
          oldPath: entryPath,
          newPath: newPath,
          relativePath: path.relative(__dirname, entryPath),
        });

        // Armazena para atualizar importações
        const relativeOldPath = path
          .relative(__dirname, entryPath)
          .replace(/\\/g, "/");
        const relativeNewPath = path
          .relative(__dirname, newPath)
          .replace(/\\/g, "/");
        renamedFiles[relativeOldPath] = relativeNewPath;
      }
    }
  });
}

// Função para atualizar importações nos arquivos
function updateImports() {
  // Procurar em todos os diretórios por arquivos para atualizar
  const allFiles = [];
  directories.forEach((dir) => {
    function findAllFiles(directory) {
      const entries = fs.readdirSync(directory, { withFileTypes: true });
      entries.forEach((entry) => {
        const entryPath = path.join(directory, entry.name);
        if (
          entry.isDirectory() &&
          entry.name !== "node_modules" &&
          !entry.name.startsWith(".")
        ) {
          findAllFiles(entryPath);
        } else if (entry.name.endsWith(".js") || entry.name.endsWith(".jsx")) {
          allFiles.push(entryPath);
        }
      });
    }
    findAllFiles(dir);
  });

  // Processar cada arquivo para atualizar importações
  allFiles.forEach((filePath) => {
    let content = fs.readFileSync(filePath, "utf8");
    let updated = false;

    // Verifica cada arquivo renomeado para atualizar importações
    for (const [oldRelativePath, newRelativePath] of Object.entries(
      renamedFiles
    )) {
      // Remover extensão para comparação com importações
      const oldPathWithoutExt = oldRelativePath.replace(".js", "");
      const newPathWithoutExt = newRelativePath.replace(".jsx", "");

      // Padrões de importação para procurar
      const importPatterns = [
        new RegExp(
          `from ['"](.*/)?${path.basename(oldPathWithoutExt)}['"]`,
          "g"
        ),
        new RegExp(
          `import ['"](.*/)?${path.basename(oldPathWithoutExt)}['"]`,
          "g"
        ),
      ];

      importPatterns.forEach((pattern) => {
        // Substitui o padrão mantendo o mesmo caminho relativo
        content = content.replace(pattern, (match) => {
          updated = true;
          // Preserva o resto do caminho e apenas atualiza o nome do arquivo
          return match.replace(
            path.basename(oldPathWithoutExt),
            path.basename(newPathWithoutExt)
          );
        });
      });
    }

    if (updated) {
      fs.writeFileSync(filePath, content, "utf8");
      console.log(
        `Atualizadas importações em: ${path.relative(__dirname, filePath)}`
      );
    }
  });
}

// Função principal
function renameJSXFiles() {
  console.log("Iniciando detecção de arquivos JSX...");

  // Processar todos os diretórios
  directories.forEach((dir) => {
    if (fs.existsSync(dir)) {
      processDirectory(dir);
    }
  });

  console.log(
    `Encontrados ${filesToUpdate.length} arquivos com JSX para renomear:`
  );

  // Renomear arquivos
  filesToUpdate.forEach((file) => {
    try {
      fs.renameSync(file.oldPath, file.newPath);
      console.log(
        `Renomeado: ${file.relativePath} -> ${path.basename(file.newPath)}`
      );
    } catch (error) {
      console.error(`Erro ao renomear ${file.oldPath}:`, error);
    }
  });

  // Atualizar importações
  if (filesToUpdate.length > 0) {
    console.log("\nAtualizando importações...");
    updateImports();
  }

  console.log("\nProcesso concluído!");
}

// Executa a função principal
renameJSXFiles();
