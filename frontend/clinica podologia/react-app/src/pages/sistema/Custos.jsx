import React, { useState, useEffect } from "react";
import { success } from "../../services/toastService";
import "./Custos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";

const ModalVisualizarCusto = ({ estaAberto, aoFechar, custo }) => {
  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes do custo</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
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
                <span className="valor">{custo.valor}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">É fixo?</span>
                <span className="valor">{custo.isFixo ? "Sim" : "Não"}</span>
              </div>
            </div>

            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Data:</span>
                <span className="valor">{custo.data}</span>
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

const ModalCustos = ({ estaAberto, aoFechar, custo, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState({
    nome: "",
    descricao: "",
    valor: "",
    data: "",
    isFixo: false,
  });

  useEffect(() => {
    if (estaAberto) {
      if (custo && custo.id !== undefined) {
        setDadosFormulario({ ...custo });
      } else {
        setDadosFormulario({
          nome: "",
          descricao: "",
          valor: "",
          data: "",
          isFixo: false,
        });
      }
    }
  }, [custo, estaAberto]);

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
          <h2>{custo.id ? "Editar Custo" : "Adicionar Custo"}</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nome">Nome</label>
              <input
                type="text"
                id="nome"
                name="nome"
                value={dadosFormulario.nome}
                onChange={alterarCampo}
                required
              />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="descricao">Descrição</label>
              <input
                type="text"
                id="descricao"
                name="descricao"
                value={dadosFormulario.descricao}
                onChange={alterarCampo}
                required
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="valor">Valor</label>
              <input
                type="number"
                id="valor"
                name="valor"
                value={dadosFormulario.valor}
                onChange={alterarCampo}
                required
              />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="isFixo">É custo fixo?</label>
              <input
                type="checkbox"
                id="isFixo"
                name="isFixo"
                checked={dadosFormulario.isFixo}
                onChange={alterarCampo}
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="data">Data</label>
              <input
                type="text"
                id="data"
                name="data"
                placeholder="DD/MM/AAAA"
                value={dadosFormulario.data}
                onChange={alterarCampo}
                required
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

const Custos = () => {
  const [listaCustos, setListaCustos] = useState([
    {
      id: 1,
      nome: "Aluguel",
      descricao: "Custo fixo mensal do aluguel da clínica",
      valor: "2000",
      data: "01/09/2023",
      isFixo: true,
    },
  ]);

  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] = useState(false);

  const [custoEmEdicao, setCustoEmEdicao] = useState({});
  const [custoParaVisualizar, setCustoParaVisualizar] = useState({});
  const [custoParaExcluir, setCustoParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  const adicionarCusto = () => {
    setCustoEmEdicao({
      nome: "",
      descricao: "",
      valor: "",
      data: "",
      isFixo: false,
    });
    setModalEditarAberto(true);
  };

  const visualizarCusto = (custo) => {
    setCustoParaVisualizar(custo);
    setModalVisualizarAberto(true);
  };

  const editarCusto = (custo) => {
    setCustoEmEdicao(custo);
    setModalEditarAberto(true);
  };

  const prepararExclusao = (custoId) => {
    setCustoParaExcluir(custoId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  const confirmarExclusao = () => {
    if (custoParaExcluir) {
      setListaCustos(listaCustos.filter((c) => c.id !== custoParaExcluir));
      success("Custo excluído com sucesso!");
      setModalConfirmacaoExclusaoAberto(false);
      setCustoParaExcluir(null);
    }
  };

  const salvarCusto = (dadosCusto) => {
    if (dadosCusto.id) {
      const custosAtualizados = listaCustos.map((c) =>
        c.id === dadosCusto.id ? { ...c, ...dadosCusto } : c
      );
      setListaCustos(custosAtualizados);
      success("Custo editado com sucesso!");
    } else {
      const novoCusto = { id: Date.now(), ...dadosCusto };
      setListaCustos([...listaCustos, novoCusto]);
      success("Custo adicionado com sucesso!");
    }
  };

  const custosFiltrados = listaCustos.filter((c) =>
    c.nome.toLowerCase().includes(termoPesquisa.toLowerCase())
  );

  return (
    <div className="container-custos">
      <h1>Custos</h1>

      <div className="container-pesquisa">
        <input
          type="text"
          placeholder="Pesquisar..."
          className="campo-pesquisa"
          value={termoPesquisa}
          onChange={(e) => setTermoPesquisa(e.target.value)}
        />
        <button className="botao-adicionar" onClick={adicionarCusto}>
          Adicionar custo
        </button>
      </div>

      <div className="tabela-custos">
        <table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>Descrição</th>
              <th>Valor</th>
              <th>Tipo de custo</th>
              <th>Data</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {custosFiltrados.map((custo) => (
              <tr key={custo.id}>
                <td>{custo.nome}</td>
                <td>{custo.descricao}</td>
                <td>R$ {custo.valor}</td>
                <td>{custo.isFixo ? "Fixo" : "Variável"}</td>
                <td>{custo.data}</td>
                <td>
                  <div className="acoes-tabela">
                    <button
                      className="botao-tabela-visualizar"
                      onClick={() => visualizarCusto(custo)}
                    >
                      Visualizar
                    </button>
                    <button
                      className="botao-tabela-editar"
                      onClick={() => editarCusto(custo)}
                    >
                      Editar
                    </button>
                    <button
                      className="botao-tabela-excluir"
                      onClick={() => prepararExclusao(custo.id)}
                    >
                      Excluir
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {custosFiltrados.length === 0 && (
        <div className="sem-resultados">
          <p>Nenhum custo encontrado.</p>
        </div>
      )}

      {/* Modais */}
      <ModalCustos
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        custo={custoEmEdicao}
        aoSalvar={salvarCusto}
      />

      <ModalVisualizarCusto
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        custo={custoParaVisualizar}
      />

      <ModalConfirmacao
        estaAberto={modalConfirmacaoExclusaoAberto}
        aoFechar={() => setModalConfirmacaoExclusaoAberto(false)}
        aoConfirmar={confirmarExclusao}
        titulo="Confirmar exclusão"
        mensagem="Tem certeza que deseja excluir este custo? Esta ação não pode ser desfeita."
        textoBotaoConfirmar="Excluir"
        textoBotaoCancelar="Cancelar"
        tipo="exclusao"
      />
    </div>
  );
};

export default Custos;
