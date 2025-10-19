import React, { useState } from "react";
import NotificacaoToast from "../../components/sistema/NotificacaoToast";
import "./Clientes.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";

// Componente Modal para Visualização de Cliente
const ModalVisualizarCliente = ({ estaAberto, aoFechar, cliente }) => {
  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes do Cliente</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <div className="conteudo-visualizacao">
          <div className="grupo-visualizacao">
            <h3>Informações Pessoais</h3>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Nome completo:</span>
                <span className="valor">{cliente.nomeCompleto}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">CPF:</span>
                <span className="valor">{cliente.cpf}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Email:</span>
                <span className="valor">{cliente.email}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Telefone:</span>
                <span className="valor">{cliente.telefone}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Data de Nascimento:</span>
                <span className="valor">{cliente.dataNascimento}</span>
              </div>
            </div>
          </div>

          {/* Aqui podem ser adicionados outros grupos de informações no futuro, como histórico de atendimentos */}
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

// Componente Modal para Edição/Adição de Cliente
const ModalCliente = ({ estaAberto, aoFechar, cliente, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState(
    cliente || {
      nomeCompleto: "",
      cpf: "",
      email: "",
      telefone: "",
      dataNascimento: "",
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
          <h2>{cliente.id ? "Editar Cliente" : "Adicionar Cliente"}</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nomeCompleto">Nome completo</label>
              <input
                type="text"
                id="nomeCompleto"
                name="nomeCompleto"
                value={dadosFormulario.nomeCompleto}
                onChange={alterarCampo}
                required
              />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="cpf">CPF</label>
              <input
                type="text"
                id="cpf"
                name="cpf"
                value={dadosFormulario.cpf}
                onChange={alterarCampo}
                required
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                name="email"
                value={dadosFormulario.email}
                onChange={alterarCampo}
                required
              />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="telefone">Telefone</label>
              <input
                type="tel"
                id="telefone"
                name="telefone"
                value={dadosFormulario.telefone}
                onChange={alterarCampo}
                required
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="dataNascimento">Data de nascimento</label>
              <input
                type="text"
                id="dataNascimento"
                name="dataNascimento"
                placeholder="DD/MM/AAAA"
                value={dadosFormulario.dataNascimento}
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

const Clientes = () => {
  // Estado para armazenar a lista de clientes
  const [listaClientes, setListaClientes] = useState([
    {
      id: 1,
      nomeCompleto: "Fulano da Silva",
      cpf: "Fulano da Silva", // Mantido conforme a imagem
      email: "fulano@gmail.com",
      telefone: "(00) 90000-0000",
      dataNascimento: "01/01/2000",
    },
  ]);

  // Estados para os modais
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] =
    useState(false);
  const [clienteEmEdicao, setClienteEmEdicao] = useState({});
  const [clienteParaVisualizar, setClienteParaVisualizar] = useState({});
  const [clienteParaExcluir, setClienteParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  // Função para adicionar um novo cliente
  const adicionarCliente = () => {
    setClienteEmEdicao({
      nomeCompleto: "",
      cpf: "",
      email: "",
      telefone: "",
      dataNascimento: "",
    });
    setModalEditarAberto(true);
  };

  // Função para visualizar um cliente
  const visualizarCliente = (cliente) => {
    setClienteParaVisualizar({ ...cliente });
    setModalVisualizarAberto(true);
  };

  // Função para editar um cliente existente
  const editarCliente = (cliente) => {
    setClienteEmEdicao({ ...cliente });
    setModalEditarAberto(true);
  };

  // Função para preparar a exclusão de um cliente (abre o modal)
  const prepararExclusao = (clienteId) => {
    setClienteParaExcluir(clienteId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  // Função para confirmar a exclusão do cliente
  const confirmarExclusao = () => {
    if (clienteParaExcluir) {
      setListaClientes(
        listaClientes.filter((cliente) => cliente.id !== clienteParaExcluir)
      );
      setModalConfirmacaoExclusaoAberto(false);
      setClienteParaExcluir(null);
    }
  };

  // Estado para notificação toast
  const [notificacaoVisivel, setNotificacaoVisivel] = useState(false);
  const [mensagemNotificacao, setMensagemNotificacao] = useState("");

  // Função para salvar um cliente (novo ou editado)
  const salvarCliente = (dadosCliente) => {
    if (dadosCliente.id) {
      // Atualizar cliente existente
      const clientesAtualizados = listaClientes.map((cliente) =>
        cliente.id === dadosCliente.id
          ? { ...cliente, ...dadosCliente }
          : cliente
      );
      setListaClientes(clientesAtualizados);
      setMensagemNotificacao("Cliente atualizado com sucesso!");
    } else {
      // Adicionar novo cliente
      const novoCliente = {
        id: Date.now(), // ID temporário
        ...dadosCliente,
      };
      setListaClientes([...listaClientes, novoCliente]);
      setMensagemNotificacao("Cliente adicionado com sucesso!");
    }
    setNotificacaoVisivel(true);
  };

  // Filtrar clientes com base no termo de pesquisa
  const clientesFiltrados = listaClientes.filter(
    (cliente) =>
      cliente.nomeCompleto
        .toLowerCase()
        .includes(termoPesquisa.toLowerCase()) ||
      cliente.email.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      cliente.cpf.includes(termoPesquisa)
  );

  return (
    <div className="container-clientes">
      <h1>Clientes</h1>

      <div className="container-pesquisa">
        <input
          type="text"
          placeholder="Pesquisar..."
          className="campo-pesquisa"
          value={termoPesquisa}
          onChange={(e) => setTermoPesquisa(e.target.value)}
        />
        <button className="botao-adicionar" onClick={adicionarCliente}>
          Adicionar Cliente
        </button>
      </div>

      {/* Lista de clientes em formato de tabela */}
      <div className="tabela-clientes">
        <table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>CPF</th>
              <th>Email</th>
              <th>Telefone</th>
              <th>Data Nasc.</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {clientesFiltrados.map((cliente) => (
              <tr key={cliente.id}>
                <td>{cliente.nomeCompleto}</td>
                <td>{cliente.cpf}</td>
                <td>{cliente.email}</td>
                <td>{cliente.telefone}</td>
                <td>{cliente.dataNascimento}</td>
                <td>
                  <div className="acoes-tabela">
                    <button
                      className="botao-tabela-visualizar"
                      onClick={() => visualizarCliente(cliente)}
                    >
                      Visualizar
                    </button>
                    <button
                      className="botao-tabela-editar"
                      onClick={() => editarCliente(cliente)}
                    >
                      Editar
                    </button>
                    <button
                      className="botao-tabela-excluir"
                      onClick={() => prepararExclusao(cliente.id)}
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

      {clientesFiltrados.length === 0 && (
        <div className="sem-resultados">
          <p>Nenhum cliente encontrado.</p>
        </div>
      )}

      {/* Modal para adicionar/editar cliente */}
      <ModalCliente
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        cliente={clienteEmEdicao}
        aoSalvar={salvarCliente}
      />

      {/* Modal para visualizar detalhes do cliente */}
      <ModalVisualizarCliente
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        cliente={clienteParaVisualizar}
      />

      {/* Modal de confirmação de exclusão */}
      <ModalConfirmacao
        estaAberto={modalConfirmacaoExclusaoAberto}
        aoFechar={() => setModalConfirmacaoExclusaoAberto(false)}
        aoConfirmar={confirmarExclusao}
        titulo="Confirmar exclusão"
        mensagem="Tem certeza que deseja excluir este cliente? Esta ação não pode ser desfeita."
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

export default Clientes;
