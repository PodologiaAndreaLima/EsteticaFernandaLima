// Executa os cenários de teste do ETL de nota fiscal contra o backend local e imprime um resumo.
// Uso: node rodar-cenarios.mjs <pasta-das-imagens> <email> <senha>
// Pré-requisitos: backend rodando em http://localhost:8080 e Node 18+ (fetch nativo).
// Os uploads são espaçados para não estourar o limite de 5 uploads por minuto.
import fs from "node:fs";
import path from "node:path";

const [, , pasta, email, senha] = process.argv;
if (!pasta || !email || !senha) {
  console.error("Uso: node rodar-cenarios.mjs <pasta> <email> <senha>");
  process.exit(1);
}

const BASE = "http://localhost:8080";
const ESPERA_ENTRE_UPLOADS_MS = 13000;
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
const resultados = [];
let token = "";
let primeiroUpload = true;

async function login() {
  const r = await fetch(`${BASE}/usuarios/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, senha }),
  });
  token = (await r.json()).token;
  if (!token) throw new Error("Login falhou");
}

const autenticado = () => ({ Authorization: `Bearer ${token}` });

async function json(r) {
  const texto = await r.text();
  try {
    return JSON.parse(texto);
  } catch {
    return texto;
  }
}

async function upload(arquivo, mime) {
  if (!primeiroUpload) await sleep(ESPERA_ENTRE_UPLOADS_MS);
  primeiroUpload = false;
  const buf = fs.readFileSync(path.join(pasta, arquivo));
  const form = new FormData();
  form.append("imagem", new Blob([buf], { type: mime }), arquivo);
  const r = await fetch(`${BASE}/estoque/notas`, { method: "POST", headers: autenticado(), body: form });
  return { http: r.status, corpo: await json(r) };
}

async function consultar(id) {
  const r = await fetch(`${BASE}/estoque/notas/${id}`, { headers: autenticado() });
  return { http: r.status, corpo: await json(r) };
}

// Espera a extração terminar (PROCESSANDO sai) ou até 90 s.
async function aguardar(id) {
  for (let i = 0; i < 30; i++) {
    const { corpo } = await consultar(id);
    if (corpo.status !== "PROCESSANDO") return corpo;
    await sleep(3000);
  }
  return { status: "TIMEOUT" };
}

// Confirma a nota: itens VALIDO viram CRIAR_NOVO; itens INVALIDO são rejeitados.
// Com rejeitarTudo, todos são rejeitados. Com soValidos, omite os inválidos (deve dar 400).
async function confirmar(nota, { rejeitarTudo = false, soValidos = false } = {}) {
  const decisoes = [];
  for (const it of nota.itens) {
    if (it.status === "INVALIDO" || rejeitarTudo) {
      if (soValidos) continue;
      decisoes.push({ itemId: it.itemId, acao: "REJEITAR", criarNovo: false });
    } else {
      decisoes.push({
        itemId: it.itemId, acao: "CRIAR_NOVO", criarNovo: true,
        nomeNovo: it.textoOriginal, quantidade: it.quantidade, valorUnitario: it.valorUnitario,
      });
    }
  }
  const r = await fetch(`${BASE}/estoque/notas/${nota.notaId}/confirmar`, {
    method: "POST",
    headers: { ...autenticado(), "Content-Type": "application/json" },
    body: JSON.stringify({ itens: decisoes }),
  });
  return { http: r.status, corpo: await json(r) };
}

async function descartar(id) {
  const r = await fetch(`${BASE}/estoque/notas/${id}/descartar`, { method: "POST", headers: autenticado() });
  return { http: r.status };
}

function registrar(caso, esperado, http, corpo, observacao = "") {
  const itens = Array.isArray(corpo?.itens) ? corpo.itens.map((i) => i.status).join("/") : "";
  resultados.push({
    caso,
    esperado,
    http,
    notaId: corpo?.notaId ?? null,
    status: corpo?.status ?? corpo?.codigo ?? "",
    codigoFalha: corpo?.codigoFalha ?? corpo?.codigo ?? "",
    avisos: Array.isArray(corpo?.avisos) ? corpo.avisos.join(",") : "",
    itens,
    observacao,
  });
}

async function main() {
  await login();
  // Cópia byte a byte de c02 para testar a deduplicação por imagem.
  fs.copyFileSync(path.join(pasta, "c02-completa-sem-chave.png"), path.join(pasta, "c05-copia-identica.png"));

  // Sucesso e deduplicação
  let r = await upload("c01-completa-com-chave.png", "image/png");
  const c01 = await aguardar(r.corpo.notaId);
  registrar("C01 completa com chave", "EM_REVISAO", r.http, c01);
  const c01Conf = await confirmar(c01);
  registrar("C01 confirmar (criar novos)", "CONFIRMADA 200", c01Conf.http, c01Conf.corpo);

  r = await upload("c02-completa-sem-chave.png", "image/png");
  const c02 = await aguardar(r.corpo.notaId);
  registrar("C02 completa sem chave", "EM_REVISAO", r.http, c02);
  const c02Conf = await confirmar(c02);
  registrar("C02 confirmar (criar novos)", "CONFIRMADA 200", c02Conf.http, c02Conf.corpo);

  r = await upload("c04-mesma-nota-sem-chave-outra-foto.png", "image/png");
  const c04 = await aguardar(r.corpo.notaId);
  registrar("C04 mesma nota sem chave, outra foto", "EM_REVISAO com POSSIVEL_DUPLICATA", r.http, c04);

  r = await upload("c03-chave-repetida-outra-foto.png", "image/png");
  const c03 = await aguardar(r.corpo.notaId);
  registrar("C03 mesma chave, outra foto", "FALHA DUPLICADA", r.http, c03);

  r = await upload("c05-copia-identica.png", "image/png");
  registrar("C05 mesma imagem de C02", "409 DUPLICADA no upload", r.http, r.corpo);

  // Chave de acesso e dados faltando
  for (const [arquivo, caso, esperado] of [
    ["c06-chave-dv-errado.png", "C06 chave com DV errado", "EM_REVISAO com CHAVE_NAO_CONFIRMADA"],
    ["c07-sem-total.png", "C07 sem total", "FALHA NOTA_INVALIDA"],
    ["c08-sem-data.png", "C08 sem data", "FALHA NOTA_INVALIDA"],
    ["c09-data-futura.png", "C09 data futura", "FALHA NOTA_INVALIDA"],
  ]) {
    r = await upload(arquivo, "image/png");
    const corpo = await aguardar(r.corpo.notaId);
    registrar(caso, esperado, r.http, corpo);
  }

  // Qualidade da imagem
  for (const [arquivo, caso, esperado] of [
    ["c10-ilegivel.png", "C10 imagem ilegível", "FALHA IMAGEM_ILEGIVEL (ou outro, conforme o Gemini)"],
    ["c20-nao-e-nota.png", "C20 foto que não é nota", "FALHA (IMAGEM_ILEGIVEL ou NOTA_INVALIDA)"],
  ]) {
    r = await upload(arquivo, "image/png");
    const corpo = await aguardar(r.corpo.notaId);
    registrar(caso, esperado, r.http, corpo);
  }

  // Avisos e itens inconsistentes
  r = await upload("c12-total-divergente.png", "image/png");
  const c12 = await aguardar(r.corpo.notaId);
  registrar("C12 total divergente", "EM_REVISAO com TOTAL_DIVERGENTE", r.http, c12);

  r = await upload("c13-cnpj-invalido.png", "image/png");
  const c13 = await aguardar(r.corpo.notaId);
  registrar("C13 CNPJ inválido", "EM_REVISAO com CNPJ_INVALIDO", r.http, c13);

  r = await upload("c11-um-item-inconsistente.png", "image/png");
  const c11 = await aguardar(r.corpo.notaId);
  registrar("C11 um item inconsistente", "EM_REVISAO com item INVALIDO", r.http, c11);
  const c11Parcial = await confirmar(c11, { soValidos: true });
  registrar("C11 confirmar sem decidir item inválido", "400 SOLICITACAO_INVALIDA", c11Parcial.http, c11Parcial.corpo);
  const c11Conf = await confirmar(c11);
  registrar("C11 confirmar (rejeita inválido, cria o válido)", "CONFIRMADA 200", c11Conf.http, c11Conf.corpo);

  r = await upload("c12b-todos-itens-inconsistentes.png", "image/png");
  const c12b = await aguardar(r.corpo.notaId);
  registrar("C12b todos os itens inconsistentes", "FALHA NOTA_INVALIDA", r.http, c12b);

  r = await upload("c19-item-sem-quantidade.png", "image/png");
  const c19 = await aguardar(r.corpo.notaId);
  registrar("C19 item sem quantidade", "EM_REVISAO com item INVALIDO", r.http, c19);

  // Rejeitar tudo e descarte
  r = await upload("c18-para-rejeitar-tudo.png", "image/png");
  const c18 = await aguardar(r.corpo.notaId);
  const c18Conf = await confirmar(c18, { rejeitarTudo: true });
  registrar("C18 rejeitar todos os itens", "CONFIRMADA sem movimentos", c18Conf.http, c18Conf.corpo);

  r = await upload("c17-para-descarte.png", "image/png");
  const c17 = await aguardar(r.corpo.notaId);
  const c17Desc = await descartar(c17.notaId);
  registrar("C17 descartar em EM_REVISAO", "DESCARTADA 204", c17Desc.http, { notaId: c17.notaId, status: "DESCARTADA" });
  const c17Pos = await confirmar(c17);
  registrar("C17 confirmar após descartar", "409 CONFLITO_ESTADO", c17Pos.http, c17Pos.corpo);

  // Entradas inválidas
  r = await upload("c14-texto.txt", "text/plain");
  registrar("C14 arquivo que não é imagem", "400 IMAGEM_INVALIDA", r.http, r.corpo);

  r = await upload("c15-maior-que-5mb.png", "image/png");
  registrar("C15 imagem maior que 5 MB", "400 ou 413 (limite de upload)", r.http, r.corpo);

  const inexistente = await consultar(999999);
  registrar("Consulta de nota inexistente", "404", inexistente.http, inexistente.corpo);

  const semToken = await fetch(`${BASE}/estoque/notas/1`);
  registrar("Consulta sem token", "401", semToken.status, {});

  // Limite de uploads: 6 envios seguidos, sem espera, para forçar o 429
  const bufTexto = fs.readFileSync(path.join(pasta, "c14-texto.txt"));
  for (let i = 1; i <= 6; i++) {
    const form = new FormData();
    form.append("imagem", new Blob([bufTexto], { type: "text/plain" }), "c14-texto.txt");
    const rr = await fetch(`${BASE}/estoque/notas`, { method: "POST", headers: autenticado(), body: form });
    registrar(`Rajada de upload ${i}/6`, i < 6 ? "400 (ainda dentro do limite)" : "429 LIMITE_EXCEDIDO", rr.status, await json(rr));
  }

  const saida = path.join(pasta, "resultados.json");
  fs.writeFileSync(saida, JSON.stringify(resultados, null, 2));
  console.log(`| Caso | Esperado | HTTP | notaId | Status/código | Avisos | Itens |`);
  console.log(`|---|---|---|---|---|---|---|`);
  for (const x of resultados) {
    console.log(`| ${x.caso} | ${x.esperado} | ${x.http} | ${x.notaId ?? ""} | ${x.status} ${x.codigoFalha && x.codigoFalha !== x.status ? x.codigoFalha : ""} | ${x.avisos} | ${x.itens} |`);
  }
  console.log(`\nResultados salvos em ${saida}`);
}

main().catch((e) => {
  console.error("Falha na execução:", e);
  process.exit(1);
});
