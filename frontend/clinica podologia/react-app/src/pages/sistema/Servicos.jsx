import React, { useState } from "react";
import "./Servicos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";

// Componente Modal para Visualização de Serviço
const ModalVisualizarServico = ({ estaAberto, aoFechar, servico }) => {
  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes do Serviço</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <div className="conteudo-visualizacao">
          <div className="grupo-visualizacao">
            <h3>Informações do Serviço</h3>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Nome do serviço:</span>
                <span className="valor">{servico.nome}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Descrição:</span>
                <span className="valor">{servico.descricao}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de custo/despesa:</span>
                <span className="valor">R$ {servico.valorCusto}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de venda:</span>
                <span className="valor">R$ {servico.valorVenda}</span>
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

// Componente Modal para Edição/Adição de Serviço
const ModalServico = ({ estaAberto, aoFechar, servico, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState(
    servico || {
      nome: "",
      descricao: "",
      valorCusto: "",
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
          <h2>{servico.id ? "Editar Serviço" : "Adicionar Serviço"}</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nome">Nome do serviço</label>
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
              <label htmlFor="valorCusto">Valor de custo/despesa (R$)</label>
              <input
                type="text"
                id="valorCusto"
                name="valorCusto"
                value={dadosFormulario.valorCusto}
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

const Servicos = () => {
  // Estado para armazenar a lista de serviços
  const [listaServicos, setListaServicos] = useState([
    {
      id: 1,
      nome: "Micropigmentação",
      descricao: "Procedimento que implanta pigmento na pele para corrigir falhas, definir o formato e realçar o seu olhar. O objetivo é proporcionar sobrancelhas mais harmoniosas e expressivas, dispensando a maquiagem diária.",
      valorCusto: "100,00",
      valorVenda: "300,00",
    },
    {
      id: 2,
      nome: "Podologia Avançada",
      descricao: "Tratamento completo dos pés com foco em saúde e estética, incluindo remoção de calosidades, tratamento de unhas encravadas e orientação preventiva.",
      valorCusto: "50,00",
      valorVenda: "150,00",
    }
  ]);

  // Estados para os modais
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] = useState(false);
  const [servicoEmEdicao, setServicoEmEdicao] = useState({});
  const [servicoParaVisualizar, setServicoParaVisualizar] = useState({});
  const [servicoParaExcluir, setServicoParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  // Função para adicionar um novo serviço
  const adicionarServico = () => {
    setServicoEmEdicao({
      nome: "",
      descricao: "",
      valorCusto: "",
      valorVenda: "",
    });
    setModalEditarAberto(true);
  };

  // Função para visualizar um serviço
  const visualizarServico = (servico) => {
    setServicoParaVisualizar({ ...servico });
    setModalVisualizarAberto(true);
  };

  // Função para editar um serviço existente
  const editarServico = (servico) => {
    setServicoEmEdicao({ ...servico });
    setModalEditarAberto(true);
  };

  // Função para preparar a exclusão de um serviço (abre o modal)
  const prepararExclusao = (servicoId) => {
    setServicoParaExcluir(servicoId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  // Função para confirmar a exclusão do serviço
  const confirmarExclusao = () => {
    if (servicoParaExcluir) {
      setListaServicos(
        listaServicos.filter((servico) => servico.id !== servicoParaExcluir)
      );
      setModalConfirmacaoExclusaoAberto(false);
      setServicoParaExcluir(null);
    }
  };

  // Função para salvar um serviço (novo ou editado)
  const salvarServico = (dadosServico) => {
    if (dadosServico.id) {
      // Atualizar serviço existente
      const servicosAtualizados = listaServicos.map((servico) =>
        servico.id === dadosServico.id
          ? { ...servico, ...dadosServico }
          : servico
      );
      setListaServicos(servicosAtualizados);
    } else {
      // Adicionar novo serviço
      const novoServico = {
        id: Date.now(), // ID temporário
        ...dadosServico,
      };
      setListaServicos([...listaServicos, novoServico]);
    }
  };

  // Filtrar serviços com base no termo de pesquisa
  const servicosFiltrados = listaServicos.filter(
    (servico) =>
      servico.nome.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      servico.descricao.toLowerCase().includes(termoPesquisa.toLowerCase())
  );

  return (
    <div className="container-servicos">
      <h1>Serviços</h1>

      <div className="container-pesquisa">
        <input
          type="text"
          placeholder="Pesquisar..."
          className="campo-pesquisa"
          value={termoPesquisa}
          onChange={(e) => setTermoPesquisa(e.target.value)}
        />
        <button className="botao-adicionar" onClick={adicionarServico}>
          Adicionar Serviço
        </button>
      </div>

      {/* Lista de serviços em formato de tabela */}
      <div className="tabela-servicos">
        <table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>Descrição</th>
              <th>Valor de Custo</th>
              <th>Valor de Venda</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {servicosFiltrados.map((servico) => (
              <tr key={servico.id}>
                <td>{servico.nome}</td>
                <td className="descricao-truncada">{servico.descricao}</td>
                <td>R$ {servico.valorCusto}</td>
                <td>R$ {servico.valorVenda}</td>
                <td>
                  <div className="acoes-tabela">
                    <button
                      className="botao-tabela-visualizar"
                      onClick={() => visualizarServico(servico)}
                    >
                      Visualizar
                    </button>
                    <button
                      className="botao-tabela-editar"
                      onClick={() => editarServico(servico)}
                    >
                      Editar
                    </button>
                    <button
                      className="botao-tabela-excluir"
                      onClick={() => prepararExclusao(servico.id)}
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

      {servicosFiltrados.length === 0 && (
        <div className="sem-resultados">
          <p>Nenhum serviço encontrado.</p>
        </div>
      )}

      {/* Modal para adicionar/editar serviço */}
      <ModalServico
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        servico={servicoEmEdicao}
        aoSalvar={salvarServico}
      />

      {/* Modal para visualizar detalhes do serviço */}
      <ModalVisualizarServico
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        servico={servicoParaVisualizar}
      />

      {/* Modal de confirmação de exclusão */}
      <ModalConfirmacao
        estaAberto={modalConfirmacaoExclusaoAberto}
        aoFechar={() => setModalConfirmacaoExclusaoAberto(false)}
        aoConfirmar={confirmarExclusao}
        titulo="Confirmar exclusão"
        mensagem="Tem certeza que deseja excluir este serviço? Esta ação não pode ser desfeita."
        textoBotaoConfirmar="Excluir"
        textoBotaoCancelar="Cancelar"
        tipo="exclusao"
      />
    </div>
  );
};

export default Servicos;