import React, { useState, useEffect } from "react";
import { success, error, promise } from "../../services/toastService";
import CatalogService from "../../services/catalogService";
import "./Servicos.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import ServiceCard from "../../components/sistema/ServiceCard";

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
  // Inicia com formulário vazio e sincroniza quando o modal abre
  const [dadosFormulario, setDadosFormulario] = useState({
    nome: "",
    descricao: "",
    valorCusto: "",
    valorVenda: "",
  });

  React.useEffect(() => {
    if (estaAberto) {
      if (servico && servico.id !== undefined) {
        setDadosFormulario({ ...servico });
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
  // Estado para armazenar a lista de serviços (fonte: CatalogService)
  const [listaServicos, setListaServicos] = useState(
    CatalogService.getServicos()
  );

  useEffect(() => {
    const unsub = CatalogService.subscribeServicos((next) =>
      setListaServicos(next)
    );
    return () => unsub();
  }, []);
  //   id: 2,
  //   nome: "Peeling Facial",
  //   descricao:
  //     "Tratamento estético que promove a renovação celular da pele através da aplicação de substâncias esfoliantes. Remove células mortas, melhora textura e luminosidade, minimiza linhas finas e ajuda a controlar a oleosidade.",
  //   valorCusto: "70,00",
  //   valorVenda: "220,00",
  // },
  // {
  //   id: 3,
  //   nome: "Lash Lifting",
  //   descricao:
  //     "Lash lifting é um procedimento estético que curva e alonga os cílios naturais, criando um efeito similar ao uso de curvex e máscara mas com durabilidade prolongada. A técnica não utiliza fio sintéticos, mas sim aplica produtos específicos para moldar os cílios.",
  //   valorCusto: "",
  //   valorVenda: "160,00",
  // },
  // {
  //   id: 4,
  //   nome: "Limpeza de Pele",
  //   descricao:
  //     "Limpeza de pele é um procedimento estético que remove profundamente impurezas, células mortas e excesso de oleosidade da pele, como cravos e espinhas.",
  //   valorCusto: "",
  //   valorVenda: "150,00",
  // },

  // Estados para os modais
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] =
    useState(false);
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
      const updated = listaServicos.filter(
        (servico) => servico.id !== servicoParaExcluir
      );
      setListaServicos(updated);
      CatalogService.setServicos(updated);
      success("Serviço excluído com sucesso!");
      setModalConfirmacaoExclusaoAberto(false);
      setServicoParaExcluir(null);
    }
  };

  // Notificações agora via react-hot-toast (toastService)

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
      CatalogService.setServicos(servicosAtualizados);
      // Exibe notificação ao editar
      success("Serviço editado com sucesso!");
    } else {
      // Adicionar novo serviço
      const novoServico = {
        id: Date.now(), // ID temporário
        ...dadosServico,
      };
      const updated = [...listaServicos, novoServico];
      setListaServicos(updated);
      CatalogService.setServicos(updated);
      success("Serviço adicionado com sucesso!");
    }
    // toast shown via success()
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

      {/* Lista de serviços em formato de cards */}
      <div className="grid-cards">
        {servicosFiltrados.map((servico) => (
          <ServiceCard
            key={servico.id}
            servico={servico}
            onVisualizar={visualizarServico}
            onEditar={editarServico}
            onExcluir={prepararExclusao}
          />
        ))}
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
      {/* notifications handled by react-hot-toast (Toaster is global) */}
    </div>
  );
};

export default Servicos;
