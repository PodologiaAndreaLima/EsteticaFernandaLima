import React, { useState } from "react";
import NotificacaoToast from "../../components/sistema/NotificacaoToast";
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
  // Estado para armazenar a lista de serviços
  const [listaServicos, setListaServicos] = useState([
    {
      id: 1,
      nome: "Micropigmentação",
      descricao:
        "Micropigmentação de sobrancelhas é uma técnica estética que deposita pigmentos na pele para preencher falhas, definir e realçar a sobrancelha, resultando em um visual mais denso e harmonioso. Ao contrário de tatuagens, ela é semipermanente, sendo aplicada em uma camada superficial da pele e exigindo retoques periódicos para manter o resultado.",
      valorCusto: "",
      valorVenda: "599,00",
    },
    {
      id: 4,
      nome: "Design de Sobrancelhas",
      descricao:
        "Design de sobrancelhas é um procedimento estético que modela e realça o formato natural das sobrancelhas, respeitando as proporções do rosto e as características individuais da pessoa.",
      valorCusto: "",
      valorVenda: "60,00",
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
    // {
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
  ]);

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
      setListaServicos(
        listaServicos.filter((servico) => servico.id !== servicoParaExcluir)
      );
      setMensagemNotificacao("Serviço excluído com sucesso!");
      setNotificacaoVisivel(true);
      setModalConfirmacaoExclusaoAberto(false);
      setServicoParaExcluir(null);
    }
  };

  // Estado para notificação toast
  const [notificacaoVisivel, setNotificacaoVisivel] = useState(false);
  const [mensagemNotificacao, setMensagemNotificacao] = useState("");

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
      // Exibe notificação ao editar
      setMensagemNotificacao("Serviço editado com sucesso!");
    } else {
      // Adicionar novo serviço
      const novoServico = {
        id: Date.now(), // ID temporário
        ...dadosServico,
      };
      setListaServicos([...listaServicos, novoServico]);
      setMensagemNotificacao("Serviço adicionado com sucesso!");
    }
    setNotificacaoVisivel(true);
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
      {/* Notificação Toast */}
      <NotificacaoToast
        mensagem={mensagemNotificacao}
        visivel={notificacaoVisivel}
        aoFechar={() => setNotificacaoVisivel(false)}
      />
    </div>
  );
};

export default Servicos;
