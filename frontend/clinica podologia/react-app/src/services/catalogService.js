// Serviço simples em memória para armazenar catálogo de serviços e produtos
// Fornece getters, setters e um mecanismo de subscription para atualizar componentes

let servicos = [
  {
    id: 1,
    nome: "Micropigmentação",
    descricao:
      "Micropigmentação de sobrancelhas é uma técnica estética que deposita pigmentos na pele para preencher falhas, definir e realçar a sobrancelha.",
    valorCusto: "",
    valorVenda: "599,00",
  },
  {
    id: 5,
    nome: "Design de Sobrancelhas com Tintura",
    descricao:
      "Inclui o procedimento de design tradicional com aplicação de tintura para realçar ainda mais o formato e a cor das sobrancelhas.",
    valorCusto: "",
    valorVenda: "65,00",
  },
  {
    id: 6,
    nome: "Brow Lamination",
    descricao:
      "Brow Lamination é uma técnica de design de sobrancelhas que alinha os fios, criando um efeito volumoso e preenchido.",
    valorCusto: "",
    valorVenda: "150,00",
  },
];

let produtos = [
  {
    id: 1,
    nome: "Creme facial",
    descricao:
      "Marca: XPTO, Categoria: XPTO, use 1x ao dia, serve para tratar acne e espinhas",
    marca: "XPTO",
    categoria: "Cuidados faciais",
    valorCompra: "20,00",
    valorVenda: "100,00",
  },
  {
    id: 2,
    nome: "Hidratante para pés",
    descricao:
      "Hidratante especial para calcanhares ressecados e rachados, uso diário.",
    marca: "PodoSkin",
    categoria: "Cuidados com os pés",
    valorCompra: "15,00",
    valorVenda: "45,00",
  },
];

const servicosListeners = new Set();
const produtosListeners = new Set();

const notifyServicos = () =>
  servicosListeners.forEach((fn) => fn([...servicos]));
const notifyProdutos = () =>
  produtosListeners.forEach((fn) => fn([...produtos]));

const CatalogService = {
  // Servicos
  getServicos: () => [...servicos],
  setServicos: (list) => {
    servicos = list ? [...list] : [];
    notifyServicos();
  },
  addServico: (servico) => {
    servicos = [...servicos, servico];
    notifyServicos();
  },
  updateServico: (updated) => {
    servicos = servicos.map((s) =>
      s.id === updated.id ? { ...s, ...updated } : s
    );
    notifyServicos();
  },
  removeServico: (id) => {
    servicos = servicos.filter((s) => s.id !== id);
    notifyServicos();
  },
  subscribeServicos: (fn) => {
    servicosListeners.add(fn);
    // return unsubscribe
    return () => servicosListeners.delete(fn);
  },

  // Produtos
  getProdutos: () => [...produtos],
  setProdutos: (list) => {
    produtos = list ? [...list] : [];
    notifyProdutos();
  },
  addProduto: (produto) => {
    produtos = [...produtos, produto];
    notifyProdutos();
  },
  updateProduto: (updated) => {
    produtos = produtos.map((p) =>
      p.id === updated.id ? { ...p, ...updated } : p
    );
    notifyProdutos();
  },
  removeProduto: (id) => {
    produtos = produtos.filter((p) => p.id !== id);
    notifyProdutos();
  },
  subscribeProdutos: (fn) => {
    produtosListeners.add(fn);
    return () => produtosListeners.delete(fn);
  },
};

export default CatalogService;
