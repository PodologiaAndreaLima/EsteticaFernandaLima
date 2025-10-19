import React, { useState } from "react";
import NotificacaoToast from "../../components/sistema/NotificacaoToast";
import "./Produtos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";

// Componente Modal para Visualização de Produto
const ModalVisualizarProduto = ({ estaAberto, aoFechar, produto }) => {
  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes do Produto</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <div className="conteudo-visualizacao">
          <div className="grupo-visualizacao">
            <h3>Informações do Produto</h3>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Nome do produto:</span>
                <span className="valor">{produto.nome}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Descrição:</span>
                <span className="valor">{produto.descricao}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Marca:</span>
                <span className="valor">{produto.marca}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Categoria:</span>
                <span className="valor">{produto.categoria}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de compra:</span>
                <span className="valor">R$ {produto.valorCompra}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de venda:</span>
                <span className="valor">R$ {produto.valorVenda}</span>
              </div>
            </div>
          </div>
        </div>

        <div className="rodape-modal">
          <button className="botao-fechar-visualizacao" onClick={aoFechar}>
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
};

// Componente Modal para Edição/Adição de Produto
const ModalProduto = ({ estaAberto, aoFechar, produto, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState(
    produto || {
      nome: "",
      descricao: "",
      marca: "",
      categoria: "",
      valorCompra: "",
      valorVenda: "",
    }
  );

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    setDadosFormulario({
      ...dadosFormulario,
      [name]: value,
    });
  };

  const enviarFormulario = (e) => {
    e.preventDefault();
    aoSalvar(dadosFormulario);
    aoFechar();
  };

  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>{produto.id ? "Editar Produto" : "Adicionar Produto"}</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nome">Nome do produto</label>
              <input
                type="text"
                id="nome"
                name="nome"
                value={dadosFormulario.nome}
                onChange={alterarCampo}
                required
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="marca">Marca</label>
              <input
                type="text"
                id="marca"
                name="marca"
                value={dadosFormulario.marca}
                onChange={alterarCampo}
                required
              />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="categoria">Categoria</label>
              <input
                type="text"
                id="categoria"
                name="categoria"
                value={dadosFormulario.categoria}
                onChange={alterarCampo}
                required
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="descricao">Descrição</label>
              <textarea
                id="descricao"
                name="descricao"
                value={dadosFormulario.descricao}
                onChange={alterarCampo}
                rows="4"
                required
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="valorCompra">Valor de compra (R$)</label>
              <input
                type="text"
                id="valorCompra"
                name="valorCompra"
                value={dadosFormulario.valorCompra}
                onChange={alterarCampo}
                required
                placeholder="0,00"
              />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="valorVenda">Valor de venda (R$)</label>
              <input
                type="text"
                id="valorVenda"
                name="valorVenda"
                value={dadosFormulario.valorVenda}
                onChange={alterarCampo}
                required
                placeholder="0,00"
              />
            </div>
          </div>

          <div className="rodape-modal">
            <button type="button" className="botao-cancelar" onClick={aoFechar}>
              Cancelar
            </button>
            <button type="submit" className="botao-salvar">
              Salvar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const Produtos = () => {
  // Estado para armazenar a lista de produtos
  const [listaProdutos, setListaProdutos] = useState([
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
  ]);

  // Estados para os modais
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] =
    useState(false);
  const [produtoEmEdicao, setProdutoEmEdicao] = useState({});
  const [produtoParaVisualizar, setProdutoParaVisualizar] = useState({});
  const [produtoParaExcluir, setProdutoParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  // Função para adicionar um novo produto
  const adicionarProduto = () => {
    setProdutoEmEdicao({
      nome: "",
      descricao: "",
      marca: "",
      categoria: "",
      valorCompra: "",
      valorVenda: "",
    });
    setModalEditarAberto(true);
  };

  // Função para visualizar um produto
  const visualizarProduto = (produto) => {
    setProdutoParaVisualizar({ ...produto });
    setModalVisualizarAberto(true);
  };

  // Função para editar um produto existente
  const editarProduto = (produto) => {
    setProdutoEmEdicao({ ...produto });
    setModalEditarAberto(true);
  };

  // Função para preparar a exclusão de um produto (abre o modal)
  const prepararExclusao = (produtoId) => {
    setProdutoParaExcluir(produtoId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  // Função para confirmar a exclusão do produto
  const confirmarExclusao = () => {
    if (produtoParaExcluir) {
      setListaProdutos(
        listaProdutos.filter((produto) => produto.id !== produtoParaExcluir)
      );
      setMensagemNotificacao("Produto excluído com sucesso!");
      setNotificacaoVisivel(true);
      setModalConfirmacaoExclusaoAberto(false);
      setProdutoParaExcluir(null);
    }
  };

  // Estado para notificação toast
  const [notificacaoVisivel, setNotificacaoVisivel] = useState(false);
  const [mensagemNotificacao, setMensagemNotificacao] = useState("");

  // Função para salvar um produto (novo ou editado)
  const salvarProduto = (dadosProduto) => {
    if (dadosProduto.id) {
      // Atualizar produto existente
      const produtosAtualizados = listaProdutos.map((produto) =>
        produto.id === dadosProduto.id
          ? { ...produto, ...dadosProduto }
          : produto
      );
      setListaProdutos(produtosAtualizados);
      // Não exibe notificação ao editar
    } else {
      // Adicionar novo produto
      const novoProduto = {
        id: Date.now(), // ID temporário
        ...dadosProduto,
      };
      setListaProdutos([...listaProdutos, novoProduto]);
      setMensagemNotificacao("Produto adicionado com sucesso!");
    }
    setNotificacaoVisivel(true);
  };

  // Filtrar produtos com base no termo de pesquisa
  const produtosFiltrados = listaProdutos.filter(
    (produto) =>
      produto.nome.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      produto.descricao.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      produto.marca.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      produto.categoria.toLowerCase().includes(termoPesquisa.toLowerCase())
  );

  return (
    <div className="container-produtos">
      <h1>Produtos</h1>

      <div className="container-pesquisa">
        <input
          type="text"
          placeholder="Pesquisar..."
          className="campo-pesquisa"
          value={termoPesquisa}
          onChange={(e) => setTermoPesquisa(e.target.value)}
        />
        <button className="botao-adicionar" onClick={adicionarProduto}>
          Adicionar Produto
        </button>
      </div>

      {/* Lista de produtos em formato de cards */}
      <div className="grid-cards">
        {produtosFiltrados.map((produto) => (
          <div key={produto.id} className="card-produto">
            <div className="card-header">
              <h3>{produto.nome}</h3>
              <span className="card-categoria">{produto.categoria}</span>
            </div>
            <div className="card-content">
              <p className="card-marca">
                <span>Marca:</span> {produto.marca}
              </p>
              <div className="card-valores">
                <div className="valor-item">
                  <span>Compra:</span>
                  <strong>R$ {produto.valorCompra}</strong>
                </div>
                <div className="valor-item">
                  <span>Venda:</span>
                  <strong>R$ {produto.valorVenda}</strong>
                </div>
              </div>
              <p className="card-descricao-truncada">{produto.descricao}</p>
            </div>
            <div className="card-actions">
              <button
                className="card-button-visualizar"
                onClick={() => visualizarProduto(produto)}
              >
                Visualizar
              </button>
              <button
                className="card-button-editar"
                onClick={() => editarProduto(produto)}
              >
                Editar
              </button>
              <button
                className="card-button-excluir"
                onClick={() => prepararExclusao(produto.id)}
              >
                Excluir
              </button>
            </div>
          </div>
        ))}
      </div>

      {produtosFiltrados.length === 0 && (
        <div className="sem-resultados">
          <p>Nenhum produto encontrado.</p>
        </div>
      )}

      {/* Modal para adicionar/editar produto */}
      <ModalProduto
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        produto={produtoEmEdicao}
        aoSalvar={salvarProduto}
      />

      {/* Modal para visualizar detalhes do produto */}
      <ModalVisualizarProduto
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        produto={produtoParaVisualizar}
      />

      {/* Modal de confirmação de exclusão */}
      <ModalConfirmacao
        estaAberto={modalConfirmacaoExclusaoAberto}
        aoFechar={() => setModalConfirmacaoExclusaoAberto(false)}
        aoConfirmar={confirmarExclusao}
        titulo="Confirmar exclusão"
        mensagem="Tem certeza que deseja excluir este produto? Esta ação não pode ser desfeita."
        textoBotaoConfirmar="Excluir"
        textoBotaoCancelar="Cancelar"
        tipo="exclusao"
      />
      {/* Notificação Toast */}
      <NotificacaoToast
        mensagem={mensagemNotificacao}
        visivel={notificacaoVisivel}
        aoFechar={() => setNotificacaoVisivel(false)}
      />
    </div>
  );
};

export default Produtos;
