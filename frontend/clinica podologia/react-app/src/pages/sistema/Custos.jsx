import React, { useState, useEffect, useCallback } from "react";
import { success, error } from "../../services/toastService";
import "./Custos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import custosService from "../../services/custosService"; // extras
import custoFixoService from "../../services/custoFixoService"; // fixos
import { useRoleProtection } from "../../hooks/useRoleProtection";
import { useAuth } from "../../contexts/AuthContext";

// helpers de data
const ddmmyyyyToIso = (s) => {
  if (!s) return null;
  const parts = s.split("/");
  if (parts.length !== 3) return null;
  const [dd, mm, yyyy] = parts;
  return `${yyyy.padStart(4, "0")}-${mm.padStart(2, "0")}-${dd.padStart(2, "0")}`;
};

// Converte ISO (YYYY-MM-DD ou YYYY-MM-DDTHH:MM:SS), Date ou strings DD/MM/YYYY para "DD/MM/YYYY"
const isoToDdmmyyyy = (input) => {
  if (!input) return "";

  // Se já for Date
  if (input instanceof Date && !isNaN(input)) {
    const d = input;
    const dd = String(d.getDate()).padStart(2, "0");
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const yyyy = d.getFullYear();
    return `${dd}/${mm}/${yyyy}`;
  }

  // Se for string
  if (typeof input === "string") {
    // Caso: já esteja no formato DD/MM/YYYY (ex: "15/12/2025")
    if (input.includes("/")) {
      const parts = input.split("/");
      if (parts.length === 3) {
        const [dd, mm, yyyy] = parts;
        // validação simples: se os pedaços parecem números
        if (!isNaN(dd) && !isNaN(mm) && !isNaN(yyyy)) {
          return `${String(dd).padStart(2, "0")}/${String(mm).padStart(2, "0")}/${String(yyyy).padStart(4, "0")}`;
        }
      }
      return "";
    }

    // Caso: formato ISO ou com tempo "2025-12-15" ou "2025-12-15T00:00:00"
    const datePart = input.split("T")[0]; // pega antes do 'T' se houver
    const parts = datePart.split("-");
    if (parts.length === 3) {
      const [yyyy, mm, dd] = parts;
      if (!isNaN(dd) && !isNaN(mm) && !isNaN(yyyy)) {
        return `${String(dd).padStart(2, "0")}/${String(mm).padStart(2, "0")}/${String(yyyy).padStart(4, "0")}`;
      }
    }
  }

  // fallback: tenta parsear com Date
  try {
    const d = new Date(input);
    if (!isNaN(d)) {
      return isoToDdmmyyyy(d);
    }
  } catch (e) {
    // nada
  }

  return "";
};

// Modal Visualizar
const ModalVisualizarCusto = ({ estaAberto, aoFechar, custo }) => {
  if (!estaAberto) return null;
  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes do custo</h2>
          <button className="botao-fechar" onClick={aoFechar}>&times;</button>
        </div>
        <div className="conteudo-visualizacao">
          <div className="grupo-visualizacao">
            <h3>Informações do custo</h3>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Nome:</span>
                <span className="valor">{custo.nome}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Descrição:</span>
                <span className="valor">{custo.descricao}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Valor:</span>
                <span className="valor">R$ {custo.valor}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Tipo:</span>
                <span className="valor">{custo.isFixo ? "Fixo" : "Extra"}</span>
              </div>
            </div>
            {custo.data && (
              <div className="linha-visualizacao">
                <div className="campo-visualizacao">
                  <span className="rotulo">Data:</span>
                  <span className="valor">{custo.data}</span>
                </div>
              </div>
            )}
          </div>
        </div>
        <div className="rodape-modal">
          <button className="botao-fechar-visualizacao" onClick={aoFechar}>Fechar</button>
        </div>
      </div>
    </div>
  );
};

// Modal Criar/Editar
const ModalCustos = ({ estaAberto, aoFechar, custo, aoSalvar, abaAtiva }) => {
  const [dadosFormulario, setDadosFormulario] = useState({
    nome: "",
    descricao: "",
    valor: "",
    data: "",
    id: undefined,
  });

  useEffect(() => {
    if (estaAberto) {
      if (custo && custo.id !== undefined) {
        setDadosFormulario({
          nome: custo.nome ?? "",
          descricao: custo.descricao ?? "",
          valor: custo.valor ?? "",
          data: custo.data ?? "",
          id: custo.id,
        });
      } else {
        setDadosFormulario({
          nome: "",
          descricao: "",
          valor: "",
          data: "",
          id: undefined,
        });
      }
    }
  }, [custo, estaAberto, abaAtiva]);

  const alterarCampo = (e) => {
    const { name, type, value, checked } = e.target;
    setDadosFormulario({
      ...dadosFormulario,
      [name]: type === "checkbox" ? checked : value,
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
          <h2>{dadosFormulario.id ? "Editar Custo" : `Adicionar ${abaAtiva === "fixos" ? "Custo Fixo" : "Custo Extra"}`}</h2>
          <button className="botao-fechar" onClick={aoFechar}>&times;</button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nome">Nome</label>
              <input type="text" id="nome" name="nome" value={dadosFormulario.nome} onChange={alterarCampo} required />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="descricao">Descrição</label>
              <input type="text" id="descricao" name="descricao" value={dadosFormulario.descricao} onChange={alterarCampo} />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="valor">{abaAtiva === "fixos" ? "Valor mensal (R$)" : "Valor (R$)"}</label>
              <input type="number" id="valor" name="valor" value={dadosFormulario.valor} onChange={alterarCampo} required />
            </div>
          </div>

          {abaAtiva === "extras" && (
            <div className="linha-formulario">
              <div className="grupo-formulario">
                <label htmlFor="data">Data</label>
                <input type="text" id="data" name="data" placeholder="DD/MM/AAAA" value={dadosFormulario.data} onChange={alterarCampo} required />
              </div>
            </div>
          )}

          <div className="rodape-modal">
            <button type="button" className="botao-cancelar" onClick={aoFechar}>Cancelar</button>
            <button type="submit" className="botao-salvar">Salvar</button>
          </div>
        </form>
      </div>
    </div>
  );
};

const Custos = () => {
  // PROTEÇÃO: usar o hook para garantir apenas ADMIN veja a tela
  const userRole = useRoleProtection(["ADMIN"]);
  const { user } = useAuth();

  if (!userRole) {
    return <div>Carregando...</div>;
  }

  const [abaAtiva, setAbaAtiva] = useState("extras"); // "extras" | "fixos"
  const [listaCustos, setListaCustos] = useState([]);
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] = useState(false);

  const [custoEmEdicao, setCustoEmEdicao] = useState({});
  const [custoParaVisualizar, setCustoParaVisualizar] = useState({});
  const [custoParaExcluir, setCustoParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  const fetchCustos = useCallback(async () => {
    try {
      if (abaAtiva === "fixos") {
        const raw = await custoFixoService.list();
        console.log("fetchCustos - raw fixos:", raw);
        const mapped = (raw || []).map((it) => ({
          id: it.idCustoFixo ?? it.id,
          nome: it.nome,
          descricao: it.descricao,
          valor: it.valorMensal ?? 0,
          data: "",
          isFixo: true,
        }));
        setListaCustos(mapped);
      } else {
        const raw = await custosService.list();
        console.log("fetchCustos - raw extras:", raw);
        const mapped = (raw || []).map((it) => ({
          id: it.idCustoExtra ?? it.id,
          nome: it.nome,
          descricao: it.descricao,
          valor: it.valor ?? 0,
          data: it.data ? isoToDdmmyyyy(it.data) : "",
          isFixo: false,
        }));
        setListaCustos(mapped);
      }
    } catch (err) {
      console.error("Erro ao buscar custos", err);
      error("Erro ao buscar custos do servidor");
    }
  }, [abaAtiva]);

  useEffect(() => {
    fetchCustos();
  }, [fetchCustos]);

  const adicionarCusto = () => {
    setCustoEmEdicao({
      nome: "",
      descricao: "",
      valor: "",
      data: "",
    });
    setModalEditarAberto(true);
  };

  const visualizarCusto = (custo) => {
    setCustoParaVisualizar(custo);
    setModalVisualizarAberto(true);
  };

  const editarCusto = (custo) => {
    const dataFormatada = custo.data ? custo.data : "";
    setCustoEmEdicao({
      ...custo,
      data: dataFormatada,
    });
    setModalEditarAberto(true);
  };

  const prepararExclusao = (custoId) => {
    setCustoParaExcluir(custoId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  // confirmarExclusao
  const confirmarExclusao = async () => {
    if (!custoParaExcluir) return;
    try {
      if (abaAtiva === "fixos") {
        await custoFixoService.remove(custoParaExcluir);
      } else {
        await custosService.remove(custoParaExcluir);
      }
      success("Custo excluído com sucesso!");
      // atualiza a lista pra garantir consistência
      await fetchCustos();
    } catch (err) {
      console.error("Erro ao excluir custo", err);
      error("Erro ao excluir custo");
    } finally {
      setModalConfirmacaoExclusaoAberto(false);
      setCustoParaExcluir(null);
    }
  };

  // salvarCusto completo
  const salvarCusto = async (dadosCusto) => {
    try {
      const valorNum = parseFloat(String(dadosCusto.valor).replace(",", ".")) || 0;

      // Edição
      if (dadosCusto.id) {
        const atualLocal = listaCustos.find((c) => c.id === dadosCusto.id) || {};
        const estavaFixos = !!atualLocal.isFixo;

        if (abaAtiva === "fixos") {
          if (!estavaFixos) {
            try { await custosService.remove(dadosCusto.id); } catch (e) { }
            const payloadCreate = { nome: dadosCusto.nome, descricao: dadosCusto.descricao, valorMensal: valorNum };
            await custoFixoService.create(payloadCreate);
            success("Custo convertido para fixo e salvo!");
            await fetchCustos();
            return;
          } else {
            const payload = { nome: dadosCusto.nome, descricao: dadosCusto.descricao, valorMensal: valorNum };
            await custoFixoService.update(dadosCusto.id, payload);
            success("Custo fixo atualizado com sucesso!");
            await fetchCustos();
            return;
          }
        } else {
          if (estavaFixos) {
            try { await custoFixoService.remove(dadosCusto.id); } catch (e) { }
            const payloadCreate = { nome: dadosCusto.nome, descricao: dadosCusto.descricao, valor: valorNum, data: ddmmyyyyToIso(dadosCusto.data) };
            await custosService.create(payloadCreate);
            success("Custo convertido para extra e salvo!");
            await fetchCustos();
            return;
          } else {
            const payload = { nome: dadosCusto.nome, descricao: dadosCusto.descricao, valor: valorNum, data: ddmmyyyyToIso(dadosCusto.data) };
            await custosService.update(dadosCusto.id, payload);
            success("Custo extra atualizado com sucesso!");
            await fetchCustos();
            return;
          }
        }
      } else {
        // Criação
        if (abaAtiva === "fixos") {
          const payload = { nome: dadosCusto.nome, descricao: dadosCusto.descricao, valorMensal: valorNum };
          await custoFixoService.create(payload);
          success("Custo fixo adicionado com sucesso!");
          await fetchCustos();
        } else {
          const payload = { nome: dadosCusto.nome, descricao: dadosCusto.descricao, valor: valorNum, data: ddmmyyyyToIso(dadosCusto.data) };
          await custosService.create(payload);
          success("Custo extra adicionado com sucesso!");
          await fetchCustos();
        }
      }
    } catch (err) {
      console.error("Erro ao salvar custo", err);
      error("Erro ao salvar custo");
    }
  };

  const custosFiltrados = listaCustos.filter((c) =>
    c.nome.toLowerCase().includes(termoPesquisa.toLowerCase())
  );

  return (
    <div className="container-custos">
      <h1>Custos</h1>

      <div className="container-pesquisa">
        <div className="abas-custos">
          <button className={abaAtiva === "extras" ? "aba ativa" : "aba"} onClick={() => setAbaAtiva("extras")}>Extras</button>
          <button className={abaAtiva === "fixos" ? "aba ativa" : "aba"} onClick={() => setAbaAtiva("fixos")}>Fixos</button>
        </div>

        <input type="text" placeholder="Pesquisar..." className="campo-pesquisa" value={termoPesquisa} onChange={(e) => setTermoPesquisa(e.target.value)} />
        <button className="botao-adicionar" onClick={adicionarCusto}>Adicionar custo</button>
      </div>

      <div className="tabela-custos">
        <table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>Descrição</th>
              <th>Valor</th>
              <th>Tipo de custo</th>
              {abaAtiva === "extras" && <th>Data</th>}
              <th></th>
            </tr>
          </thead>
          <tbody>
            {custosFiltrados.map((custo) => (
              <tr key={custo.id}>
                <td>{custo.nome}</td>
                <td>{custo.descricao}</td>
                <td>R$ {custo.valor}</td>
                <td>{custo.isFixo ? "Fixo" : "Extra"}</td>
                {abaAtiva === "extras" && <td>{custo.data}</td>}
                <td>
                  <div className="acoes-tabela">
                    <button className="botao-tabela-visualizar" onClick={() => visualizarCusto(custo)}>Visualizar</button>
                    <button className="botao-tabela-editar" onClick={() => editarCusto(custo)}>Editar</button>
                    <button className="botao-tabela-excluir" onClick={() => prepararExclusao(custo.id)}>Excluir</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {custosFiltrados.length === 0 && (<div className="sem-resultados"><p>Nenhum custo encontrado.</p></div>)}

      <ModalCustos estaAberto={modalEditarAberto} aoFechar={() => setModalEditarAberto(false)} custo={custoEmEdicao} aoSalvar={salvarCusto} abaAtiva={abaAtiva} />
      <ModalVisualizarCusto estaAberto={modalVisualizarAberto} aoFechar={() => setModalVisualizarAberto(false)} custo={custoParaVisualizar} />
      <ModalConfirmacao estaAberto={modalConfirmacaoExclusaoAberto} aoFechar={() => setModalConfirmacaoExclusaoAberto(false)} aoConfirmar={confirmarExclusao} titulo="Confirmar exclusão" mensagem="Tem certeza que deseja excluir este custo? Esta ação não pode ser desfeita." textoBotaoConfirmar="Excluir" textoBotaoCancelar="Cancelar" tipo="exclusao" />
    </div>
  );
};

export default Custos;
