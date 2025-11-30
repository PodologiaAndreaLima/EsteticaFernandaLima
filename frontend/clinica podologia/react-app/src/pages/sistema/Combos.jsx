import React, { useState } from "react";
import { success, error, promise } from "../../services/toastService";
import "./Combos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import ComboCard from "../../components/sistema/ComboCard";

const ModalVisualizarCombo = ({ estaAberto, aoFechar, combo }) => {
  if (!estaAberto) return null;

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
                <span className="rotulo">valorFinal:</span>
                <span className="valor">{combo.valorFinal}</span>
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
  });

  React.useEffect(() => {
    if (estaAberto) {
      if (combo && combo.id !== undefined) {
        setDadosFormulario({ ...combo });
      } else {
        setDadosFormulario({
          nome: "",
          descricao: "",
          valorFinal: "",
        });
      }
    }
  }, [combo, estaAberto]);

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
          <h2>{combo.id ? "Editar Combo" : "Adicionar Combo"}</h2>
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
  const [listaCombos, setListaCombos] = useState([
    {
      id: 1,
      nome: "Combo Unha",
      descricao: "Manicure + Pedicure + Esmaltação",
      valorFinal: "80,00",
    },
  ]);

  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] = useState(false);
  const [comboEmEdicao, setComboEmEdicao] = useState({});
  const [comboParaExcluir, setComboParaExcluir] = useState(null);
  const [comboParaVisualizar, setComboParaVisualizar] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  const adicionarCombo = () => {
    setComboEmEdicao({
      nome: "",
      descricao: "",
      valorFinal: "",
    });
    setModalEditarAberto(true);
  };

  const visualizarCombo = (combo) => {
    setComboParaVisualizar({ ...combo});
    setModalVisualizarAberto(true);
  };

  const editarCombo = (combo) => {
    setComboEmEdicao({ ...combo });
    setModalEditarAberto(true);
  };

  const prepararExclusao = (comboId) => {
    setComboParaExcluir(comboId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  const confirmarExclusao = () => {
    if (comboParaExcluir){
      setListaCombos(
        listaCombos.filter((combo) => combo.id !== comboParaExcluir)
      );
      success("Combo excluído com sucesso!");
      setModalConfirmacaoExclusaoAberto(false);
      setComboParaExcluir(null);
    }
  };

  const salvarCombo = (dadosCombo) => {
    if (dadosCombo.id) {

      const combosAtualizados = listaCombos.map((combo) =>
        combo.id === dadosCombo.id ? { ...dadosCombo }
       : combo
      );
      setListaCombos(combosAtualizados);
      success("Combo atualizado com sucesso!");
    }else {
      const novoCombo = {
        id: Date.now(),
        ...dadosCombo,
      };
      setListaCombos([...listaCombos, novoCombo]);
      success("Combo adicionado com sucesso!");
      }
    }

    const combosFiltrados = listaCombos.filter((combo) =>
    combo.nome.toLowerCase().includes(termoPesquisa.toLowerCase())
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
  
        {/* Lista de produtos em formato de cards */}
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
  
        {/* Modal para adicionar/editar produto */}
        <ModalCombo
          estaAberto={modalEditarAberto}
          aoFechar={() => setModalEditarAberto(false)}
          combo={comboEmEdicao}
          aoSalvar={salvarCombo}
        />
  
        {/* Modal para visualizar detalhes do produto */}
        <ModalVisualizarCombo
          estaAberto={modalVisualizarAberto}
          aoFechar={() => setModalVisualizarAberto(false)}
          combo={comboParaVisualizar}
        />
  
        {/* Modal de confirmação de exclusão */}
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
        {/* notifications handled by react-hot-toast (Toaster is global) */}
      </div>
    );

  }

export default Combos;