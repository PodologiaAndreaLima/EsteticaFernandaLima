import React, { useState, useEffect } from "react";
import { success, error, promise } from "../../services/toastService";
import "./Funcionarios.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import { AuthService } from "../../services/authService";
import { useRoleProtection } from "../../hooks/useRoleProtection";
import { useAuth } from "../../contexts/AuthContext";
import {
  buildPasswordPolicyMessage,
  validateStrongPassword,
} from "../../utils/authErrorUtils";

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
    role: "USER",
  };

  const [dadosFormulario, setDadosFormulario] = useState(formInicial);

  React.useEffect(() => {
    if (estaAberto) {
      if (funcionario && Object.keys(funcionario).length > 0) {
        // Garantir que servicosPrestados seja um array
        setDadosFormulario({
          ...funcionario,
          servicosPrestados: funcionario.servicosPrestados || [], // ← ADICIONAR ISTO
          role: funcionario.role || "USER", // ← ADICIONAR ISTO também
        });
      } else {
        setDadosFormulario({
          nomeCompleto: "",
          cpf: "",
          telefone: "",
          servicosPrestados: [],
          email: "",
          senha: "",
          bio: "",
          role: "USER",
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

  const alterarServicos = (servico) => {
    const servicosAtuais = [...dadosFormulario.servicosPrestados];

    if (servicosAtuais.includes(servico)) {
      const novosServicos = servicosAtuais.filter((item) => item !== servico);
      setDadosFormulario({
        ...dadosFormulario,
        servicosPrestados: novosServicos,
      });
    } else {
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

    const shouldValidatePassword = !funcionario.id || !!dadosFormulario.senha;
    if (shouldValidatePassword) {
      const passwordValidation = validateStrongPassword(dadosFormulario.senha);
      if (!passwordValidation.isValid) {
        error(buildPasswordPolicyMessage(passwordValidation.missing));
        return;
      }
    }

    aoSalvar(dadosFormulario);
    aoFechar();
  };

  if (!estaAberto) return null;

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
                        checked={
                          dadosFormulario.servicosPrestados &&
                          Array.isArray(dadosFormulario.servicosPrestados) &&
                          dadosFormulario.servicosPrestados.includes(servico)
                        }
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
              <small>
                Minimo 8 caracteres com letra maiuscula, letra minuscula, numero
                e caractere especial.
              </small>
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="role">Cargo</label>
              <select
                id="role"
                name="role"
                value={dadosFormulario.role}
                onChange={alterarCampo}
                required
              >
                <option value="">Selecione um cargo</option>
                <option value="ADMIN">Administrador</option>
                <option value="USER">Usuário Comum</option>
              </select>
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
  // ADICIONADO: Proteger rota - apenas ADMIN
  const userRole = useRoleProtection(["ADMIN"]);
  const { user } = useAuth();

  if (!userRole) {
    return <div>Carregando...</div>;
  }

  // Estado para armazenar a lista de funcionários
  const [listaFuncionarios, setListaFuncionarios] = useState([]);
  const [carregando, setCarregando] = useState(true);

  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmacaoExclusaoAberto, setModalConfirmacaoExclusaoAberto] =
    useState(false);
  const [funcionarioEmEdicao, setFuncionarioEmEdicao] = useState(null);
  const [funcionarioParaVisualizar, setFuncionarioParaVisualizar] = useState(
    {},
  );
  const [funcionarioParaExcluir, setFuncionarioParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");
  const [modalEditarAberto, setModalEditarAberto] = useState(false);

  useEffect(() => {
    carregarFuncionarios();
  }, []);

  const carregarFuncionarios = async () => {
    setCarregando(true);
    const resposta = await AuthService.getUsuarios();
    if (resposta.success) {
      setListaFuncionarios(resposta.data);
      console.log("Funcionários carregados:", resposta.data);
    } else {
      error("Erro ao carregar funcionários");
    }
    setCarregando(false);
  };

  const adicionarFuncionario = () => {
    setFuncionarioEmEdicao({
      nomeCompleto: "",
      cpf: "",
      telefone: "",
      servicosPrestados: [],
      email: "",
      senha: "",
      bio: "",
      role: "USER",
    });
    setModalEditarAberto(true);
  };

  const visualizarFuncionario = (funcionario) => {
    setFuncionarioParaVisualizar({ ...funcionario });
    setModalVisualizarAberto(true);
  };

  const editarFuncionario = (funcionario) => {
    setFuncionarioEmEdicao({ ...funcionario });
    setModalEditarAberto(true);
  };

  const prepararExclusao = (funcionarioId) => {
    setFuncionarioParaExcluir(funcionarioId);
    setModalConfirmacaoExclusaoAberto(true);
  };

  const confirmarExclusao = async () => {
    if (funcionarioParaExcluir) {
      const resposta = await AuthService.deleteUsuario(funcionarioParaExcluir);

      if (resposta.success) {
        setListaFuncionarios(
          listaFuncionarios.filter(
            (funcionario) => funcionario.id !== funcionarioParaExcluir,
          ),
        );
        success("Funcionário excluído com sucesso!");
      } else {
        error(resposta.error);
      }

      setModalConfirmacaoExclusaoAberto(false);
      setFuncionarioParaExcluir(null);
    }
  };

  const salvarFuncionario = async (dadosFuncionario) => {
    try {
      let resposta;

      // Se tem ID, é edição; senão, é criação
      if (dadosFuncionario.id) {
        // EDITAR
        resposta = await AuthService.updateUsuario(dadosFuncionario.id, {
          nomeCompleto: dadosFuncionario.nomeCompleto,
          cpf: dadosFuncionario.cpf,
          telefone: dadosFuncionario.telefone,
          servicosPrestados: dadosFuncionario.servicosPrestados,
          email: dadosFuncionario.email,
          bio: dadosFuncionario.bio,
          role: dadosFuncionario.role,
          // Nota: senha não é enviada na edição por segurança
        });

        if (resposta.success) {
          // Atualizar lista local
          setListaFuncionarios(
            listaFuncionarios.map((func) =>
              func.id === dadosFuncionario.id ? dadosFuncionario : func,
            ),
          );
          success("Funcionário atualizado com sucesso!");
        } else {
          error(resposta.error);
        }
      } else {
        // CRIAR
        resposta = await AuthService.register({
          nomeCompleto: dadosFuncionario.nomeCompleto,
          cpf: dadosFuncionario.cpf,
          telefone: dadosFuncionario.telefone,
          servicosPrestados: dadosFuncionario.servicosPrestados,
          email: dadosFuncionario.email,
          senha: dadosFuncionario.senha,
          bio: dadosFuncionario.bio,
          role: dadosFuncionario.role,
        });

        if (resposta.success) {
          // Recarregar lista
          await carregarFuncionarios();
          success("Funcionário registrado com sucesso!");
        } else {
          error(resposta.error);
        }
      }

      setModalEditarAberto(false);
    } catch (err) {
      console.error("Erro ao salvar funcionário:", err);
      error("Erro ao salvar funcionário");
    }
  };

  const funcionariosFiltrados = listaFuncionarios.filter(
    (funcionario) =>
      // Validar se campos existem antes de chamar toLowerCase()
      (funcionario.nomeCompleto &&
        funcionario.nomeCompleto
          .toLowerCase()
          .includes(termoPesquisa.toLowerCase())) ||
      (funcionario.email &&
        funcionario.email
          .toLowerCase()
          .includes(termoPesquisa.toLowerCase())) ||
      (funcionario.cpf && funcionario.cpf.includes(termoPesquisa)) ||
      (funcionario.servicosPrestados &&
        Array.isArray(funcionario.servicosPrestados) &&
        funcionario.servicosPrestados.some(
          (servico) =>
            servico &&
            servico.toLowerCase().includes(termoPesquisa.toLowerCase()),
        )),
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

      <ModalFuncionario
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        funcionario={funcionarioEmEdicao}
        aoSalvar={salvarFuncionario}
      />

      <ModalVisualizarFuncionario
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        funcionario={funcionarioParaVisualizar}
      />

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
    </div>
  );
};

export default Funcionarios;
