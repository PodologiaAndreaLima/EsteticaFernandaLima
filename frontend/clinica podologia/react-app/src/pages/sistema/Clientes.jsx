import React, { useState, useEffect } from "react";
import { success, error } from "../../services/toastService";
import "./Clientes.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import { ClienteService } from "../../services/clienteService";

const normalizarDataParaInput = (valor) => {
  if (!valor) return "";
  const texto = String(valor);
  if (/^\d{2}\/\d{2}\/\d{4}$/.test(texto)) {
    const [dia, mes, ano] = texto.split("/");
    return `${ano}-${mes}-${dia}`;
  }
  return texto.includes("T") ? texto.split("T")[0] : texto;
};

const formatarCPF = (valor = "") => {
  const digitos = String(valor).replace(/\D/g, "").slice(0, 11);

  return digitos
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
};

const cpfEhValido = (valor = "") => {
  const cpf = String(valor).replace(/\D/g, "");
  if (cpf.length !== 11) return false;
  if (/^(\d)\1{10}$/.test(cpf)) return false;

  const calcularDigito = (base, pesoInicial) => {
    let soma = 0;
    for (let i = 0; i < base.length; i += 1) {
      soma += Number(base[i]) * (pesoInicial - i);
    }
    const resto = (soma * 10) % 11;
    return resto === 10 ? 0 : resto;
  };

  const digito1 = calcularDigito(cpf.slice(0, 9), 10);
  const digito2 = calcularDigito(cpf.slice(0, 10), 11);

  return digito1 === Number(cpf[9]) && digito2 === Number(cpf[10]);
};

const normalizarCpf = (valor = "") => String(valor).replace(/\D/g, "");

const normalizarEmail = (valor = "") => String(valor).trim().toLowerCase();

const obterIdCliente = (cliente = {}) =>
  cliente?.id ?? cliente?.idCliente ?? cliente?.id_cliente ?? null;

const normalizarClienteApi = (cliente = {}) => ({
  ...cliente,
  id: obterIdCliente(cliente),
  dataNascimento:
    normalizarDataParaInput(
      cliente.dataNascimento ?? cliente.data_nascimento,
    ) || "",
});

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
    const idCliente = obterIdCliente(cliente);

    if (cliente && idCliente) {
      setDadosFormulario({
        ...cliente,
        id: idCliente,
        dataNascimento: normalizarDataParaInput(cliente.dataNascimento),
      });
    } else {
      setDadosFormulario(formInicial);
    }
  }, [cliente, estaAberto]);

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    const valorFinal = name === "cpf" ? formatarCPF(value) : value;

    setDadosFormulario((prev) => ({
      ...prev,
      [name]: valorFinal,
    }));
  };

  const enviarFormulario = (e) => {
    e.preventDefault();

    const cpfAtual = normalizarCpf(dadosFormulario.cpf);
    const cpfOriginal = normalizarCpf(cliente?.cpf);
    const clienteExistente = Boolean(dadosFormulario.id);
    const cpfFoiAlterado = cpfAtual !== cpfOriginal;
    const deveValidarCpf = !clienteExistente || cpfFoiAlterado;

    if (deveValidarCpf && !cpfEhValido(dadosFormulario.cpf)) {
      error("CPF invalido. Digite um CPF valido.");
      return;
    }

    aoSalvar(dadosFormulario);
  };

  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>{dadosFormulario.id ? "Editar Cliente" : "Adicionar Cliente"}</h2>
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
                maxLength={14}
                inputMode="numeric"
                placeholder="000.000.000-00"
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
  const [isEditandoCliente, setIsEditandoCliente] = useState(false);

  useEffect(() => {
    carregarClientes();
  }, []);

  const carregarClientes = async () => {
    setCarregando(true);
    const resposta = await ClienteService.getClientes();
    if (resposta.success) {
      const dados = Array.isArray(resposta.data) ? resposta.data : [];
      setListaClientes(dados.map(normalizarClienteApi));
      console.log("Clientes carregados:", dados);
    } else {
      error("Erro ao carregar clientes");
      setListaClientes([]);
    }
    setCarregando(false);
  };

  const adicionarCliente = () => {
    setIsEditandoCliente(false);
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
    setClienteParaVisualizar(normalizarClienteApi(cliente));
    setModalVisualizarAberto(true);
  };

  const editarCliente = (cliente) => {
    setIsEditandoCliente(true);
    setClienteEmEdicao(normalizarClienteApi(cliente));
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
          listaClientes.filter(
            (cliente) => obterIdCliente(cliente) !== clienteParaExcluir,
          ),
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
    let idAtual = obterIdCliente(dadosCliente);
    if (!idAtual && isEditandoCliente) {
      idAtual = obterIdCliente(clienteEmEdicao);
    }

    if (!idAtual && isEditandoCliente) {
      const candidato = listaClientes.find((cliente) => {
        const mesmoCpf =
          normalizarCpf(cliente.cpf) === normalizarCpf(clienteEmEdicao?.cpf);
        const mesmoEmail =
          normalizarEmail(cliente.email) ===
          normalizarEmail(clienteEmEdicao?.email);
        return mesmoCpf || mesmoEmail;
      });
      idAtual = obterIdCliente(candidato);
    }

    const cpfInformado = normalizarCpf(dadosCliente.cpf);
    const emailInformado = normalizarEmail(dadosCliente.email);

    // Regra de negócio: bloquear duplicidade apenas no cadastro (não na edição)
    if (!isEditandoCliente) {
      const clienteDuplicado = listaClientes.find((cliente) => {
        const cpfExistente = normalizarCpf(cliente.cpf);
        const emailExistente = normalizarEmail(cliente.email);

        return (
          cpfExistente === cpfInformado || emailExistente === emailInformado
        );
      });

      if (clienteDuplicado) {
        if (normalizarCpf(clienteDuplicado.cpf) === cpfInformado) {
          error("Ja existe um cliente cadastrado com este CPF.");
          return;
        }

        error("Ja existe um cliente cadastrado com este email.");
        return;
      }
    }

    try {
      let resposta;

      if (isEditandoCliente) {
        if (!idAtual) {
          error("Nao foi possivel identificar o cliente para edicao.");
          return;
        }

        const dataNascimentoNormalizada = normalizarDataParaInput(
          dadosCliente.dataNascimento,
        );

        const cpfAtual = normalizarCpf(dadosCliente.cpf);
        const cpfOriginal = normalizarCpf(clienteEmEdicao?.cpf);
        const cpfFoiAlterado = cpfAtual !== cpfOriginal;

        const payloadAtualizacao = {
          nomeCompleto: dadosCliente.nomeCompleto,
          email: dadosCliente.email,
          telefone: dadosCliente.telefone,
          dataNascimento: dataNascimentoNormalizada,
          data_nascimento: dataNascimentoNormalizada,
        };

        if (cpfFoiAlterado) {
          payloadAtualizacao.cpf = cpfAtual;
        }

        resposta = await ClienteService.updateCliente(
          idAtual,
          payloadAtualizacao,
        );

        if (resposta.success) {
          setListaClientes(
            listaClientes.map((cliente) =>
              String(obterIdCliente(cliente)) === String(idAtual)
                ? { ...dadosCliente, id: idAtual }
                : cliente,
            ),
          );
          success("Cliente atualizado com sucesso!");
        } else {
          error(resposta.error);
        }
      } else {
        const dataNascimentoNormalizada = normalizarDataParaInput(
          dadosCliente.dataNascimento,
        );

        resposta = await ClienteService.criarCliente({
          nomeCompleto: dadosCliente.nomeCompleto,
          cpf: normalizarCpf(dadosCliente.cpf),
          email: dadosCliente.email,
          telefone: dadosCliente.telefone,
          dataNascimento: dataNascimentoNormalizada,
          data_nascimento: dataNascimentoNormalizada,
        });

        if (resposta.success) {
          await carregarClientes();
          success("Cliente adicionado com sucesso!");
        } else {
          error(resposta.error);
        }
      }

      setModalEditarAberto(false);
      setIsEditandoCliente(false);
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
            cliente.email
              .toLowerCase()
              .includes(termoPesquisa.toLowerCase())) ||
          (cliente.cpf && cliente.cpf.includes(termoPesquisa)),
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
              <tr key={obterIdCliente(cliente) || cliente.email}>
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
                      onClick={() => prepararExclusao(obterIdCliente(cliente))}
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
        aoFechar={() => {
          setModalEditarAberto(false);
          setIsEditandoCliente(false);
        }}
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
