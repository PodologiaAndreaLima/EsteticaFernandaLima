import React, { useState, useEffect } from "react";
import { success, error } from "../../services/toastService";
import "./Combos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import ComboCard from "../../components/sistema/ComboCard";
import combosService from "../../services/combosService";

const formatToFrontend = (raw) => ({
  id: raw.idCombo ?? raw.id,
  nome: raw.nome,
  descricao: raw.descricao,
  valorFinal: raw.valorFinal ?? raw.valor ?? 0,
});

const parseValorInput = (str) => {
  if (str === undefined || str === null) return 0;
  if (typeof str === "number") return str;
  const cleaned = String(str).replace(/\./g, "").replace(",", ".");
  const n = parseFloat(cleaned);
  return Number.isNaN(n) ? 0 : n;
};

const ModalVisualizarCombo = ({ estaAberto, aoFechar, combo }) => {
  if (!estaAberto || !combo) return null;
  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes do Combo</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <div className="conteudo-visualizacao">
          <div className="grupo-visualizacao">
            <h3>Informações do Combo</h3>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Nome do combo:</span>
                <span className="valor">{combo.nome}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Descrição:</span>
                <span className="valor">{combo.descricao}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Valor final:</span>
                <span className="valor">
                  {Number(combo.valorFinal).toLocaleString("pt-BR", {
                    style: "currency",
                    currency: "BRL",
                  })}
                </span>
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

const ModalCombo = ({ estaAberto, aoFechar, combo, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState({
    nome: "",
    descricao: "",
    valorFinal: "",
    id: undefined,
  });

  useEffect(() => {
    if (estaAberto) {
      if (combo && combo.id !== undefined) {
        setDadosFormulario({
          nome: combo.nome ?? "",
          descricao: combo.descricao ?? "",
          valorFinal:
            combo.valorFinal !== undefined
              ? String(combo.valorFinal).replace(".", ",")
              : "",
          id: combo.id,
        });
      } else {
        setDadosFormulario({
          nome: "",
          descricao: "",
          valorFinal: "",
          id: undefined,
        });
      }
    }
  }, [combo, estaAberto]);

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    setDadosFormulario((s) => ({ ...s, [name]: value }));
  };

  const enviarFormulario = (e) => {
    e.preventDefault();
    const payload = {
      nome: dadosFormulario.nome,
      descricao: dadosFormulario.descricao,
      valorFinal: parseValorInput(dadosFormulario.valorFinal),
    };
    aoSalvar({ ...payload, id: dadosFormulario.id });
    aoFechar();
  };

  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>{dadosFormulario.id ? "Editar Combo" : "Adicionar Combo"}</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nome">Nome do combo</label>
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
              <label htmlFor="valorFinal">Valor final (R$)</label>
              <input
                type="text"
                id="valorFinal"
                name="valorFinal"
                value={dadosFormulario.valorFinal}
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

const Combos = () => {
  const [listaCombos, setListaCombos] = useState([]);
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] =
    useState(false);
  const [comboEmEdicao, setComboEmEdicao] = useState({});
  const [comboParaExcluir, setComboParaExcluir] = useState(null);
  const [comboParaVisualizar, setComboParaVisualizar] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  const carregarCombos = async () => {
    try {
      const raw = await combosService.list();
      const mapped = (raw || []).map(formatToFrontend);
      setListaCombos(mapped);
    } catch (err) {
      console.error("Erro ao carregar combos", err);
      error("Erro ao carregar combos");
    }
  };

  useEffect(() => {
    carregarCombos();
  }, []);

  const adicionarCombo = () => {
    setComboEmEdicao({ nome: "", descricao: "", valorFinal: "" });
    setModalEditarAberto(true);
  };

  const visualizarCombo = (combo) => {
    setComboParaVisualizar(combo);
    setModalVisualizarAberto(true);
  };

  const editarCombo = (combo) => {
    setComboEmEdicao(combo);
    setModalEditarAberto(true);
  };

  const prepararExclusao = (comboId) => {
    setComboParaExcluir(comboId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  const confirmarExclusao = async () => {
    if (!comboParaExcluir) return;
    try {
      await combosService.remove(comboParaExcluir);
      success("Combo excluído com sucesso!");
      await carregarCombos();
    } catch (err) {
      console.error("Erro ao excluir combo", err);
      error("Erro ao excluir combo");
    } finally {
      setModalConfirmacaoExclusaoAberto(false);
      setComboParaExcluir(null);
    }
  };

  const salvarCombo = async (dadosCombo) => {
    try {
      if (dadosCombo.id) {
        const payload = {
          nome: dadosCombo.nome,
          descricao: dadosCombo.descricao,
          valorFinal: Number(dadosCombo.valorFinal),
        };
        await combosService.update(dadosCombo.id, payload);
        success("Combo atualizado com sucesso!");
        await carregarCombos();
      } else {
        const payload = {
          nome: dadosCombo.nome,
          descricao: dadosCombo.descricao,
          valorFinal: Number(dadosCombo.valorFinal),
        };
        await combosService.create(payload);
        success("Combo adicionado com sucesso!");
        await carregarCombos();
      }
    } catch (err) {
      console.error("Erro ao salvar combo", err);
      error("Erro ao salvar combo");
    }
  };

  const combosFiltrados = listaCombos.filter(
    (combo) =>
      combo.nome &&
      combo.nome.toLowerCase().includes(termoPesquisa.toLowerCase()),
  );

  return (
    <div className="container-combos">
      <h1>Combos</h1>

      <div className="container-pesquisa">
        <input
          type="text"
          placeholder="Pesquisar..."
          className="campo-pesquisa"
          value={termoPesquisa}
          onChange={(e) => setTermoPesquisa(e.target.value)}
        />
        <button className="botao-adicionar" onClick={adicionarCombo}>
          Adicionar Combo
        </button>
      </div>

      <div className="grid-cards">
        {combosFiltrados.map((combo) => (
          <ComboCard
            key={combo.id}
            combo={combo}
            onVisualizar={visualizarCombo}
            onEditar={editarCombo}
            onExcluir={prepararExclusao}
          />
        ))}
      </div>

      {combosFiltrados.length === 0 && (
        <div className="sem-resultados">
          <p>Nenhum combo encontrado.</p>
        </div>
      )}

      <ModalCombo
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        combo={comboEmEdicao}
        aoSalvar={salvarCombo}
      />
      <ModalVisualizarCombo
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        combo={comboParaVisualizar}
      />
      <ModalConfirmacao
        estaAberto={modalConfirmacaoExclusaoAberto}
        aoFechar={() => setModalConfirmacaoExclusaoAberto(false)}
        aoConfirmar={confirmarExclusao}
        titulo="Confirmar exclusão"
        mensagem="Tem certeza que deseja excluir este combo? Esta ação não pode ser desfeita."
        textoBotaoConfirmar="Excluir"
        textoBotaoCancelar="Cancelar"
        tipo="exclusao"
      />
    </div>
  );
};

export default Combos;
