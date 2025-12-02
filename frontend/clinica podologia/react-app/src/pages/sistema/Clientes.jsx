import React, { useState, useEffect } from "react";
import { success, error } from "../../services/toastService";
import "./Clientes.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import { ClienteService } from "../../services/clienteService";

// ✅ ADICIONAR: Componente Modal para Visualizar Cliente
const ModalVisualizarCliente = ({ estaAberto, aoFechar, cliente }) => {
  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>Detalhes do Cliente</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <div className="modal-body">
          <div className="campo-visualizacao">
            <strong>Nome:</strong>
            <p>{cliente.nomeCompleto}</p>
          </div>
          <div className="campo-visualizacao">
            <strong>CPF:</strong>
            <p>{cliente.cpf}</p>
          </div>
          <div className="campo-visualizacao">
            <strong>Email:</strong>
            <p>{cliente.email}</p>
          </div>
          <div className="campo-visualizacao">
            <strong>Telefone:</strong>
            <p>{cliente.telefone}</p>
          </div>
          <div className="campo-visualizacao">
            <strong>Data de Nascimento:</strong>
            <p>{cliente.dataNascimento}</p>
          </div>
        </div>

        <div className="rodape-modal">
          <button className="botao-cancelar" onClick={aoFechar}>
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
};

// ✅ ADICIONAR: Componente Modal para Editar/Adicionar Cliente
const ModalCliente = ({ estaAberto, aoFechar, cliente, aoSalvar }) => {
  const formInicial = {
    nomeCompleto: "",
    cpf: "",
    email: "",
    telefone: "",
    dataNascimento: "",
  };

  const [dadosFormulario, setDadosFormulario] = useState(formInicial);

  useEffect(() => {
    if (cliente && cliente.id) {
      setDadosFormulario(cliente);
    } else {
      setDadosFormulario(formInicial);
    }
  }, [cliente, estaAberto]);

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    setDadosFormulario((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const enviarFormulario = (e) => {
    e.preventDefault();
    aoSalvar(dadosFormulario);
    setDadosFormulario(formInicial);
  };

  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>
            {dadosFormulario.id ? "Editar Cliente" : "Adicionar Cliente"}
          </h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nomeCompleto">Nome Completo</label>
              <input
                type="text"
                id="nomeCompleto"
                name="nomeCompleto"
                value={dadosFormulario.nomeCompleto}
                onChange={alterarCampo}
                required
              />
            </div>
          </div>

          <div className="linha-formulario">
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
          </div>

          <div className="linha-formulario">
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
            <div className="grupo-formulario">
              <label htmlFor="dataNascimento">Data de Nascimento</label>
              <input
                type="date"
                id="dataNascimento"
                name="dataNascimento"
                value={dadosFormulario.dataNascimento}
                onChange={alterarCampo}
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

// ✅ Componente Clientes (resto do código permanece igual)
const Clientes = () => {
  const [listaClientes, setListaClientes] = useState([]);
  const [carregando, setCarregando] = useState(true);

  // Estados para os modais
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] =
    useState(false);
  const [clienteEmEdicao, setClienteEmEdicao] = useState({});
  const [clienteParaVisualizar, setClienteParaVisualizar] = useState({});
  const [clienteParaExcluir, setClienteParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  useEffect(() => {
    carregarClientes();
  }, []);

  const carregarClientes = async () => {
    setCarregando(true);
    const resposta = await ClienteService.getClientes();
    if (resposta.success) {
      const dados = Array.isArray(resposta.data) ? resposta.data : [];
      setListaClientes(dados);
      console.log("Clientes carregados:", dados);
    } else {
      error("Erro ao carregar clientes");
      setListaClientes([]);
    }
    setCarregando(false);
  };

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

  const visualizarCliente = (cliente) => {
    setClienteParaVisualizar({ ...cliente });
    setModalVisualizarAberto(true);
  };

  const editarCliente = (cliente) => {
    setClienteEmEdicao({ ...cliente });
    setModalEditarAberto(true);
  };

  const prepararExclusao = (clienteId) => {
    setClienteParaExcluir(clienteId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  const confirmarExclusao = async () => {
    if (clienteParaExcluir) {
      const resposta = await ClienteService.deleteCliente(clienteParaExcluir);

      if (resposta.success) {
        setListaClientes(
          listaClientes.filter((cliente) => cliente.id !== clienteParaExcluir)
        );
        success("Cliente excluído com sucesso!");
      } else {
        error(resposta.error);
      }

      setModalConfirmacaoExclusaoAberto(false);
      setClienteParaExcluir(null);
    }
  };

  const salvarCliente = async (dadosCliente) => {
    try {
      let resposta;

      if (dadosCliente.id) {
        resposta = await ClienteService.updateCliente(dadosCliente.id, {
          nomeCompleto: dadosCliente.nomeCompleto,
          cpf: dadosCliente.cpf,
          email: dadosCliente.email,
          telefone: dadosCliente.telefone,
          dataNascimento: dadosCliente.dataNascimento,
        });

        if (resposta.success) {
          setListaClientes(
            listaClientes.map((cliente) =>
              cliente.id === dadosCliente.id ? dadosCliente : cliente
            )
          );
          success("Cliente atualizado com sucesso!");
        } else {
          error(resposta.error);
        }
      } else {
        resposta = await ClienteService.criarCliente({
          nomeCompleto: dadosCliente.nomeCompleto,
          cpf: dadosCliente.cpf,
          email: dadosCliente.email,
          telefone: dadosCliente.telefone,
          dataNascimento: dadosCliente.dataNascimento,
        });

        if (resposta.success) {
          await carregarClientes();
          success("Cliente adicionado com sucesso!");
        } else {
          error(resposta.error);
        }
      }

      setModalEditarAberto(false);
    } catch (err) {
      console.error("Erro ao salvar cliente:", err);
      error("Erro ao salvar cliente");
    }
  };

  const clientesFiltrados = Array.isArray(listaClientes)
    ? listaClientes.filter(
        (cliente) =>
          (cliente.nomeCompleto &&
            cliente.nomeCompleto
              .toLowerCase()
              .includes(termoPesquisa.toLowerCase())) ||
          (cliente.email &&
            cliente.email.toLowerCase().includes(termoPesquisa.toLowerCase())) ||
          (cliente.cpf && cliente.cpf.includes(termoPesquisa))
      )
    : [];

  if (carregando) {
    return (
      <div className="container-clientes">
        <p>Carregando clientes...</p>
      </div>
    );
  }

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

      <ModalCliente
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        cliente={clienteEmEdicao}
        aoSalvar={salvarCliente}
      />

      <ModalVisualizarCliente
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        cliente={clienteParaVisualizar}
      />

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
    </div>
  );
};

export default Clientes;