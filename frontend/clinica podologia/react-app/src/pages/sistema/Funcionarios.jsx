import React, { useState } from "react";
import { success, error, promise } from "../../services/toastService";
import "./Funcionarios.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";

// Componente Modal para Visualização de Funcionário
const ModalVisualizarFuncionario = ({ estaAberto, aoFechar, funcionario }) => {
  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes do Funcionário</h2>
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
                <span className="valor">{funcionario.nomeCompleto}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">CPF:</span>
                <span className="valor">{funcionario.cpf}</span>
              </div>
            </div>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Email:</span>
                <span className="valor">{funcionario.email}</span>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Telefone:</span>
                <span className="valor">{funcionario.telefone}</span>
              </div>
            </div>
          </div>

          <div className="grupo-visualizacao">
            <h3>Serviços Prestados</h3>
            <div className="servicos-visualizacao">
              {funcionario.servicosPrestados &&
              funcionario.servicosPrestados.length > 0 ? (
                funcionario.servicosPrestados.map((servico, index) => (
                  <div key={index} className="servico-badge">
                    {servico}
                  </div>
                ))
              ) : (
                <p className="sem-servicos">Nenhum serviço selecionado</p>
              )}
            </div>
          </div>

          <div className="grupo-visualizacao">
            <h3>Biografia</h3>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao bio-visualizacao">
                <p>{funcionario.bio || "Biografia não informada."}</p>
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

// Componente Modal para Edição/Adição de Funcionário
const ModalFuncionario = ({ estaAberto, aoFechar, funcionario, aoSalvar }) => {
  const formInicial = {
    nomeCompleto: "",
    cpf: "",
    telefone: "",
    servicosPrestados: [],
    email: "",
    senha: "",
    bio: "",
  };

  const [dadosFormulario, setDadosFormulario] = useState(formInicial);

  // Atualiza o formulário quando o funcionário em edição muda
  React.useEffect(() => {
    if (estaAberto) {
      if (funcionario && Object.keys(funcionario).length > 0) {
        setDadosFormulario({ ...funcionario });
      } else {
        setDadosFormulario({
          nomeCompleto: "",
          cpf: "",
          telefone: "",
          servicosPrestados: [],
          email: "",
          senha: "",
          bio: "",
        });
      }
    }
  }, [estaAberto, funcionario]);

  const [mostrarSenha, setMostrarSenha] = useState(false);

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    setDadosFormulario({
      ...dadosFormulario,
      [name]: value,
    });
  };

  // Função para manipular serviços selecionados (checkbox)
  const alterarServicos = (servico) => {
    const servicosAtuais = [...dadosFormulario.servicosPrestados];

    if (servicosAtuais.includes(servico)) {
      // Remove o serviço se já estiver selecionado
      const novosServicos = servicosAtuais.filter((item) => item !== servico);
      setDadosFormulario({
        ...dadosFormulario,
        servicosPrestados: novosServicos,
      });
    } else {
      // Adiciona o serviço se não estiver selecionado
      setDadosFormulario({
        ...dadosFormulario,
        servicosPrestados: [...servicosAtuais, servico],
      });
    }
  };

  const toggleMostrarSenha = () => {
    setMostrarSenha(!mostrarSenha);
  };

  const enviarFormulario = (e) => {
    e.preventDefault();
    aoSalvar(dadosFormulario);
    aoFechar();
  };

  // Se o modal não estiver aberto, não renderiza nada
  if (!estaAberto) return null;

  // Log para depuração (você pode remover após resolver o problema)
  console.log("Modal aberto", { estaAberto, dadosFormulario });

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>
            {dadosFormulario.id
              ? "Editar Funcionário"
              : "Adicionar Funcionário"}
          </h2>
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
              <label>Serviços Prestados</label>
              <div className="servicos-container">
                {[
                  "Design de Sobrancelhas",
                  "Micropigmentação",
                  "Limpeza de Pele",
                  "Peeling Facial",
                  "Tratamento Capilar",
                  "Tratamento de Estrias",
                  "Depilação",
                ].map((servico) => (
                  <div className="servico-item" key={servico}>
                    <label className="checkbox-container">
                      <input
                        type="checkbox"
                        checked={dadosFormulario.servicosPrestados.includes(
                          servico
                        )}
                        onChange={() => alterarServicos(servico)}
                      />
                      <span className="checkbox-label">{servico}</span>
                    </label>
                  </div>
                ))}
              </div>
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
              <label htmlFor="senha">Senha</label>
              <div className="senha-container">
                <input
                  type={mostrarSenha ? "text" : "password"}
                  id="senha"
                  name="senha"
                  value={dadosFormulario.senha}
                  onChange={alterarCampo}
                  required={!funcionario.id}
                />
                <button
                  type="button"
                  className="toggle-senha"
                  onClick={toggleMostrarSenha}
                >
                  {mostrarSenha ? "👁️" : "👁️‍🗨️"}
                </button>
              </div>
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="bio">Bio</label>
              <textarea
                id="bio"
                name="bio"
                value={dadosFormulario.bio}
                onChange={alterarCampo}
                placeholder="Uma breve descrição sobre o funcionário..."
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

const Funcionarios = () => {
  // Estado para armazenar a lista de funcionários
  const [listaFuncionarios, setListaFuncionarios] = useState([
    {
      id: 1,
      nomeCompleto: "Fulano da Silva",
      cpf: "123.456.789-00",
      telefone: "(00) 90000-0000",
      servicosPrestados: ["Podologia"],
      // ...outros campos se necessário
    },
  ]);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] =
    useState(false);
  const [funcionarioEmEdicao, setFuncionarioEmEdicao] = useState(null);
  const [funcionarioParaVisualizar, setFuncionarioParaVisualizar] = useState(
    {}
  );
  const [funcionarioParaExcluir, setFuncionarioParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");
  const [modalEditarAberto, setModalEditarAberto] = useState(false);

  // Função para adicionar um novo funcionário
  const adicionarFuncionario = () => {
    setFuncionarioEmEdicao({
      nomeCompleto: "",
      cpf: "",
      telefone: "",
      servicosPrestados: [],
      email: "",
      senha: "",
      bio: "",
    });
    setModalEditarAberto(true);
  };

  // Função para visualizar um funcionário
  const visualizarFuncionario = (funcionario) => {
    setFuncionarioParaVisualizar({ ...funcionario });
    setModalVisualizarAberto(true);
  };

  // Função para editar um funcionário existente
  const editarFuncionario = (funcionario) => {
    setFuncionarioEmEdicao({ ...funcionario });
    setModalEditarAberto(true);
  };

  // Função para preparar a exclusão de um funcionário (abre o modal)
  const prepararExclusao = (funcionarioId) => {
    setFuncionarioParaExcluir(funcionarioId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  // Função para confirmar a exclusão do funcionário
  const confirmarExclusao = () => {
    if (funcionarioParaExcluir) {
      setListaFuncionarios(
        listaFuncionarios.filter(
          (funcionario) => funcionario.id !== funcionarioParaExcluir
        )
      );
      success("Funcionário excluído com sucesso!");
      setModalConfirmacaoExclusaoAberto(false);
      setFuncionarioParaExcluir(null);
    }
  };

  // Notificações agora via react-hot-toast (toastService)

  // Função para salvar um funcionário (novo ou editado)
  const salvarFuncionario = (dadosFuncionario) => {
    if (dadosFuncionario.id) {
      // Atualizar funcionário existente
      const funcionariosAtualizados = listaFuncionarios.map((funcionario) =>
        funcionario.id === dadosFuncionario.id
          ? { ...funcionario, ...dadosFuncionario }
          : funcionario
      );
      setListaFuncionarios(funcionariosAtualizados);
      // Exibe notificação ao editar
      success("Funcionário editado com sucesso!");
    } else {
      // Adicionar novo funcionário
      const novoFuncionario = {
        id: Date.now(), // ID temporário
        ...dadosFuncionario,
      };
      setListaFuncionarios([...listaFuncionarios, novoFuncionario]);
      success("Funcionário adicionado com sucesso!");
    }
  };

  // Filtrar funcionários com base no termo de pesquisa
  const funcionariosFiltrados = listaFuncionarios.filter(
    (funcionario) =>
      funcionario.nomeCompleto
        .toLowerCase()
        .includes(termoPesquisa.toLowerCase()) ||
      funcionario.email.toLowerCase().includes(termoPesquisa.toLowerCase()) ||
      funcionario.cpf.includes(termoPesquisa) ||
      funcionario.servicosPrestados.some((servico) =>
        servico.toLowerCase().includes(termoPesquisa.toLowerCase())
      )
  );

  return (
    <div className="container-funcionarios">
      <h1>Funcionários</h1>

      <div className="container-pesquisa">
        <input
          type="text"
          placeholder="Pesquisar..."
          className="campo-pesquisa"
          value={termoPesquisa}
          onChange={(e) => setTermoPesquisa(e.target.value)}
        />
        <button
          className="botao-adicionar"
          onClick={() => {
            console.log("Botão Adicionar Funcionário clicado");
            adicionarFuncionario();
          }}
        >
          Adicionar Funcionário
        </button>
      </div>

      {/* Lista de funcionários em formato de tabela */}
      <div className="tabela-funcionarios">
        <table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>CPF</th>
              <th>Telefone</th>
              <th>Email</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {funcionariosFiltrados.map((funcionario) => (
              <tr key={funcionario.id}>
                <td>{funcionario.nomeCompleto}</td>
                <td>{funcionario.cpf}</td>
                <td>{funcionario.telefone}</td>
                <td>{funcionario.email}</td>
                <td>
                  <div className="acoes-tabela">
                    <button
                      className="botao-tabela-visualizar"
                      onClick={() => visualizarFuncionario(funcionario)}
                    >
                      Visualizar
                    </button>
                    <button
                      className="botao-tabela-editar"
                      onClick={() => editarFuncionario(funcionario)}
                    >
                      Editar
                    </button>
                    <button
                      className="botao-tabela-excluir"
                      onClick={() => prepararExclusao(funcionario.id)}
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

      {funcionariosFiltrados.length === 0 && (
        <div className="sem-resultados">
          <p>Nenhum funcionário encontrado.</p>
        </div>
      )}

      {/* Modal para adicionar/editar funcionário */}
      <ModalFuncionario
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        funcionario={funcionarioEmEdicao}
        aoSalvar={salvarFuncionario}
      />

      {/* Modal para visualizar detalhes do funcionário */}
      <ModalVisualizarFuncionario
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        funcionario={funcionarioParaVisualizar}
      />

      {/* Modal de confirmação de exclusão */}
      <ModalConfirmacao
        estaAberto={modalConfirmacaoExclusaoAberto}
        aoFechar={() => setModalConfirmacaoExclusaoAberto(false)}
        aoConfirmar={confirmarExclusao}
        titulo="Confirmar exclusão"
        mensagem="Tem certeza que deseja excluir este funcionário? Esta ação não pode ser desfeita."
        textoBotaoConfirmar="Excluir"
        textoBotaoCancelar="Cancelar"
        tipo="exclusao"
      />
      {/* notifications handled by react-hot-toast (Toaster is global) */}
    </div>
  );
};

export default Funcionarios;
