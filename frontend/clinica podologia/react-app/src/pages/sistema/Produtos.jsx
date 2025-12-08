import React, { useState, useEffect } from "react";
import { success, error } from "../../services/toastService";
import "./Produtos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import ProductCard from "../../components/sistema/ProductCard";
import servicoProdutoService from "../../services/servicoProdutoService";
import produtoService from "../../services/produtoService";

/**
 * Modal de visualização
 */
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
                <span className="valor">{produto?.nome}</span>
              </div>
            </div>

            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Descrição:</span>
                <span className="valor">{produto?.descricao}</span>
              </div>
            </div>

            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Marca:</span>
                <span className="valor">{produto?.marca || "-"}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Categoria:</span>
                <span className="valor">{produto?.categoria || "-"}</span>
              </div>
            </div>

            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de compra:</span>
                <span className="valor">R$ {produto?.valorCompra}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de venda:</span>
                <span className="valor">R$ {produto?.valorVenda}</span>
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

/**
 * Modal de criação/edição
 * Observação: marca/categoria são mantidos no frontend (backend não tem campos correspondentes).
 */
const ModalProduto = ({ estaAberto, aoFechar, produto, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState({
    nome: "",
    descricao: "",
    marca: "",
    categoria: "",
    valorCompra: "",
    valorVenda: "",
    id: undefined,
  });

  useEffect(() => {
    if (estaAberto) {
      if (produto && produto.id !== undefined) {
        setDadosFormulario({
          nome: produto.nome || "",
          descricao: produto.descricao || "",
          marca: produto.marca || "",
          categoria: produto.categoria || "",
          valorCompra: produto.valorCompra ?? "",
          valorVenda: produto.valorVenda ?? "",
          id: produto.id,
        });
      } else {
        setDadosFormulario({
          nome: "",
          descricao: "",
          marca: "",
          categoria: "",
          valorCompra: "",
          valorVenda: "",
          id: undefined,
        });
      }
    }
  }, [produto, estaAberto]);

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    setDadosFormulario({ ...dadosFormulario, [name]: value });
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
          <h2>{dadosFormulario.id ? "Editar Produto" : "Adicionar Produto"}</h2>
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

/**
 * Componente principal Produtos
 */
const Produtos = () => {
  const [listaProdutos, setListaProdutos] = useState([]);
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] =
    useState(false);
  const [produtoEmEdicao, setProdutoEmEdicao] = useState({});
  const [produtoParaVisualizar, setProdutoParaVisualizar] = useState({});
  const [produtoParaExcluir, setProdutoParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  // Carrega produtos do backend
  useEffect(() => {
    fetchProdutos();
  }, []);

  const fetchProdutos = async () => {
    try {
      // Usar produtoService que já filtra apenas produtos
      const data = await produtoService.list();
      console.log("DEBUG Produtos.jsx - Produtos recebidos:", data);
      // mapeia resposta do backend para o shape do frontend
      const produtosMapeados = (data || [])
        .map((item) => ({
          id: item.idProdutoServico ?? item.id, // pega idProdutoServico do backend
          nome: item.nome,
          descricao: item.descricao,
          marca: item.marca ?? "", // backend pode não enviar
          categoria: item.categoria ?? "", // backend pode não enviar
          valorCompra: item.despesa != null ? Number(item.despesa) : 0,
          valorVenda: item.valorVenda != null ? Number(item.valorVenda) : 0,
        }));
      console.log("DEBUG Produtos.jsx - Produtos mapeados:", produtosMapeados);
      setListaProdutos(produtosMapeados);
    } catch (err) {
      console.error("Erro ao buscar produtos", err);
      error("Erro ao buscar produtos do servidor");
    }
  };

  const adicionarProduto = () => {
    setProdutoEmEdicao({});
    setModalEditarAberto(true);
  };

  const visualizarProduto = (produto) => {
    setProdutoParaVisualizar(produto);
    setModalVisualizarAberto(true);
  };

  const editarProduto = (produto) => {
    setProdutoEmEdicao(produto);
    setModalEditarAberto(true);
  };

  const prepararExclusao = (produtoId) => {
    setProdutoParaExcluir(produtoId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  const confirmarExclusao = async () => {
    if (!produtoParaExcluir) return;
    try {
      await servicoProdutoService.remove(produtoParaExcluir);
      const updated = listaProdutos.filter((p) => p.id !== produtoParaExcluir);
      setListaProdutos(updated);
      success("Produto excluído com sucesso!");
    } catch (err) {
      console.error("Erro ao excluir produto", err);
      error("Erro ao excluir produto");
    } finally {
      setModalConfirmacaoExclusaoAberto(false);
      setProdutoParaExcluir(null);
    }
  };

  const salvarProduto = async (dadosProduto) => {
    try {
      // Normaliza valores monetários
      const despesaParsed = parseFloat(
        String(dadosProduto.valorCompra).replace(",", ".").replace(/\s/g, "")
      );
      const valorVendaParsed = parseFloat(
        String(dadosProduto.valorVenda).replace(",", ".").replace(/\s/g, "")
      );

      // monta payload conforme backend espera
      const payload = {
        nome: dadosProduto.nome,
        descricao: dadosProduto.descricao || "",
        despesa: isNaN(despesaParsed) ? 0 : despesaParsed,
        valorVenda: isNaN(valorVendaParsed) ? 0 : valorVendaParsed,
        isProduto: true,
        marca: dadosProduto.marca || "",
        categoria: dadosProduto.categoria || ""
      };

      if (dadosProduto.id) {
        const atualizado = await servicoProdutoService.update(dadosProduto.id, payload);
        // mapear resposta para shape frontend (prefere valores vindos do backend)
        const atualizadoMapped = {
          id: atualizado.idProdutoServico ?? atualizado.id,
          nome: atualizado.nome,
          descricao: atualizado.descricao,
          marca: atualizado.marca ?? (dadosProduto.marca || produtoEmEdicao.marca || ""),
          categoria: atualizado.categoria ?? (dadosProduto.categoria || produtoEmEdicao.categoria || ""),
          valorCompra: atualizado.despesa ?? 0,
          valorVenda: atualizado.valorVenda ?? 0,
        };
        const novos = listaProdutos.map((p) =>
          p.id === atualizadoMapped.id ? atualizadoMapped : p
        );
        setListaProdutos(novos);
        success("Produto editado com sucesso!");
      } else {
        const criado = await servicoProdutoService.create(payload);
        const criadoMapped = {
          id: criado.idProdutoServico ?? criado.id,
          nome: criado.nome,
          descricao: criado.descricao,
          marca: criado.marca ?? (dadosProduto.marca || ""),
          categoria: criado.categoria ?? (dadosProduto.categoria || ""),
          valorCompra: criado.despesa ?? 0,
          valorVenda: criado.valorVenda ?? 0,
        };
        setListaProdutos((prev) => [...prev, criadoMapped]);
        success("Produto adicionado com sucesso!");
      }
    } catch (err) {
      console.error("Erro ao salvar produto", err);
      error("Erro ao salvar produto");
    }
  };

  const produtosFiltrados = listaProdutos.filter(
    (produto) =>
      produto.nome?.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      produto.descricao?.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      (produto.marca || "").toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      (produto.categoria || "").toLowerCase().includes(termoPesquisa.toLowerCase())
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

      <div className="grid-cards">
        {produtosFiltrados.map((produto) => (
          <ProductCard
            key={produto.id}
            produto={produto}
            onVisualizar={() => visualizarProduto(produto)}
            onEditar={() => editarProduto(produto)}
            onExcluir={() => prepararExclusao(produto.id)}
          />
        ))}
      </div>

      {produtosFiltrados.length === 0 && (
        <div className="sem-resultados">
          <p>Nenhum produto encontrado.</p>
        </div>
      )}

      <ModalProduto
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        produto={produtoEmEdicao}
        aoSalvar={salvarProduto}
      />

      <ModalVisualizarProduto
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        produto={produtoParaVisualizar}
      />

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
    </div>
  );
};

export default Produtos;
