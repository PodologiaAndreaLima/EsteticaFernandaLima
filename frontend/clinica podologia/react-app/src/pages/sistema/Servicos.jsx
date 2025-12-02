import React, { useState, useEffect } from "react";
import { success, error } from "../../services/toastService";
import "./Servicos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import ServiceCard from "../../components/sistema/ServiceCard";
import servicoProdutoService from "../../services/servicoProdutoService";

// Modais
const ModalVisualizarServico = ({ estaAberto, aoFechar, servico }) => {
  if (!estaAberto) return null;
  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes do Serviço</h2>
          <button className="botao-fechar" onClick={aoFechar}>&times;</button>
        </div>
        <div className="conteudo-visualizacao">
          <div className="grupo-visualizacao">
            <h3>Informações do Serviço</h3>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Nome do serviço:</span>
                <span className="valor">{servico?.nome}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Descrição:</span>
                <span className="valor">{servico?.descricao}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de custo/despesa:</span>
                <span className="valor">R$ {servico?.valorCusto}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de venda:</span>
                <span className="valor">R$ {servico?.valorVenda}</span>
              </div>
            </div>
          </div>
        </div>
        <div className="rodape-modal">
          <button className="botao-fechar-visualizacao" onClick={aoFechar}>Fechar</button>
        </div>
      </div>
    </div>
  );
};

const ModalServico = ({ estaAberto, aoFechar, servico, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState({
    nome: "",
    descricao: "",
    valorCusto: "",
    valorVenda: "",
  });

  useEffect(() => {
    if (estaAberto) {
      if (servico && servico.id !== undefined) {
        // garantir strings
        setDadosFormulario({
          nome: servico.nome || "",
          descricao: servico.descricao || "",
          valorCusto: servico.valorCusto ?? "",
          valorVenda: servico.valorVenda ?? "",
          id: servico.id,
        });
      } else {
        setDadosFormulario({
          nome: "",
          descricao: "",
          valorCusto: "",
          valorVenda: "",
        });
      }
    }
  }, [servico, estaAberto]);

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
          <h2>{dadosFormulario.id ? "Editar Serviço" : "Adicionar Serviço"}</h2>
          <button className="botao-fechar" onClick={aoFechar}>&times;</button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nome">Nome do serviço</label>
              <input type="text" id="nome" name="nome" value={dadosFormulario.nome} onChange={alterarCampo} required />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="descricao">Descrição</label>
              <textarea id="descricao" name="descricao" value={dadosFormulario.descricao} onChange={alterarCampo} rows="4" required />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="valorCusto">Valor de custo/despesa (R$)</label>
              <input type="text" id="valorCusto" name="valorCusto" value={dadosFormulario.valorCusto} onChange={alterarCampo} required placeholder="0,00" />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="valorVenda">Valor de venda (R$)</label>
              <input type="text" id="valorVenda" name="valorVenda" value={dadosFormulario.valorVenda} onChange={alterarCampo} required placeholder="0,00" />
            </div>
          </div>

          <div className="rodape-modal">
            <button type="button" className="botao-cancelar" onClick={aoFechar}>Cancelar</button>
            <button type="submit" className="botao-salvar">Salvar</button>
          </div>
        </form>
      </div>
    </div>
  );
};

const Servicos = () => {
  const [listaServicos, setListaServicos] = useState([]);
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] = useState(false);
  const [servicoEmEdicao, setServicoEmEdicao] = useState({});
  const [servicoParaVisualizar, setServicoParaVisualizar] = useState({});
  const [servicoParaExcluir, setServicoParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  // carregamento inicial
  useEffect(() => {
    fetchServicos();
  }, []);

  const fetchServicos = async () => {
  try {
    const data = await servicoProdutoService.list(); // usa list() que existe no service
    console.log("DEBUG - dados retornados do backend (servicoProdutoService.list):", data);

    // filtra apenas serviços (isProduto === false) — trata boolean e string
    const servicosMapeados = (data || [])
      .filter(item => item.isProduto === false || item.isProduto === "false" || item.isProduto === undefined && !item.isProduto)
      .map(item => ({
        id: item.idProdutoServico ?? item.id,
        nome: item.nome,
        descricao: item.descricao,
        valorCusto: item.despesa != null ? Number(item.despesa) : 0,
        valorVenda: item.valorVenda != null ? Number(item.valorVenda) : 0,
      }));

    setListaServicos(servicosMapeados);
  } catch (err) {
    console.error("Erro ao buscar serviços", err);
    error("Erro ao buscar serviços do servidor");
  }
};

  const adicionarServico = () => {
    setServicoEmEdicao({});
    setModalEditarAberto(true);
  };

  const visualizarServico = (servico) => {
    setServicoParaVisualizar(servico);
    setModalVisualizarAberto(true);
  };

  const editarServico = (servico) => {
    setServicoEmEdicao(servico);
    setModalEditarAberto(true);
  };

  const prepararExclusao = (servicoId) => {
    setServicoParaExcluir(servicoId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  const confirmarExclusao = async () => {
    if (!servicoParaExcluir) return;
    try {
      await servicoProdutoService.remove(servicoParaExcluir);
      const updated = listaServicos.filter((s) => s.id !== servicoParaExcluir);
      setListaServicos(updated);
      success("Serviço excluído com sucesso!");
    } catch (err) {
      console.error("Erro ao excluir serviço", err);
      error("Erro ao excluir serviço");
    } finally {
      setModalConfirmacaoExclusaoAberto(false);
      setServicoParaExcluir(null);
    }
  };

  const salvarServico = async (dadosServico) => {
  try {
    // normaliza valores monetários vindos do formulário
    const valorCustoParsed = parseFloat(String(dadosServico.valorCusto).replace(",", ".").replace(/\s/g, ""));
    const valorVendaParsed = parseFloat(String(dadosServico.valorVenda).replace(",", ".").replace(/\s/g, ""));

    // payload no formato que seu service espera (ele converte de novo em despesa/valorVenda)
    const payload = {
      nome: dadosServico.nome,
      descricao: dadosServico.descricao || "",
      valorCusto: isNaN(valorCustoParsed) ? 0 : valorCustoParsed,
      valorVenda: isNaN(valorVendaParsed) ? 0 : valorVendaParsed,
      // NOTA: não precisa setar isProduto aqui — o service já define isProduto: false na toBackendPayload
    };

    if (dadosServico.id) {
      const atualizado = await servicoProdutoService.update(dadosServico.id, payload);
      // mapeia resposta do backend pro shape do frontend
      const atualizadoMapped = {
        id: atualizado.idProdutoServico ?? atualizado.id,
        nome: atualizado.nome,
        descricao: atualizado.descricao,
        valorCusto: atualizado.despesa ?? 0,
        valorVenda: atualizado.valorVenda ?? 0,
      };
      const novos = listaServicos.map((s) => (s.id === atualizadoMapped.id ? atualizadoMapped : s));
      setListaServicos(novos);
      success("Serviço editado com sucesso!");
    } else {
      const criado = await servicoProdutoService.create(payload);
      const criadoMapped = {
        id: criado.idProdutoServico ?? criado.id,
        nome: criado.nome,
        descricao: criado.descricao,
        valorCusto: criado.despesa ?? 0,
        valorVenda: criado.valorVenda ?? 0,
      };
      setListaServicos((prev) => [...prev, criadoMapped]);
      success("Serviço adicionado com sucesso!");
    }
  } catch (err) {
    console.error("Erro ao salvar serviço", err);
    error("Erro ao salvar serviço");
  }
};

  const servicosFiltrados = listaServicos.filter(
    (servico) =>
      servico.nome?.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      servico.descricao?.toLowerCase().includes(termoPesquisa.toLowerCase())
  );

  return (
    <div className="container-servicos">
      <h1>Serviços</h1>

      <div className="container-pesquisa">
        <input type="text" placeholder="Pesquisar..." className="campo-pesquisa"
          value={termoPesquisa} onChange={(e) => setTermoPesquisa(e.target.value)} />
        <button className="botao-adicionar" onClick={adicionarServico}>Adicionar Serviço</button>
      </div>

      <div className="grid-cards">
        {servicosFiltrados.map((servico) => (
          <ServiceCard
            key={servico.id}
            servico={servico}
            onVisualizar={() => visualizarServico(servico)}
            onEditar={() => editarServico(servico)}
            onExcluir={() => prepararExclusao(servico.id)}
          />
        ))}
      </div>

      {servicosFiltrados.length === 0 && (
        <div className="sem-resultados"><p>Nenhum serviço encontrado.</p></div>
      )}

      <ModalServico estaAberto={modalEditarAberto} aoFechar={() => setModalEditarAberto(false)}
        servico={servicoEmEdicao} aoSalvar={salvarServico} />

      <ModalVisualizarServico estaAberto={modalVisualizarAberto} aoFechar={() => setModalVisualizarAberto(false)}
        servico={servicoParaVisualizar} />

      <ModalConfirmacao estaAberto={modalConfirmacaoExclusaoAberto} aoFechar={() => setModalConfirmacaoExclusaoAberto(false)}
        aoConfirmar={confirmarExclusao} titulo="Confirmar exclusão"
        mensagem="Tem certeza que deseja excluir este serviço? Esta ação não pode ser desfeita."
        textoBotaoConfirmar="Excluir" textoBotaoCancelar="Cancelar" tipo="exclusao" />
    </div>
  );
};

export default Servicos;
