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

const SERVICOS_DISPONIVEIS = [
  "Design de Sobrancelhas",
  "Micropigmentação",
  "Limpeza de Pele",
  "Peeling Facial",
  "Tratamento Capilar",
  "Tratamento de Estrias",
  "Depilação",
];

const normalizarTexto = (valor = "") =>
  String(valor)
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[_-]+/g, " ")
    .replace(/\s+/g, " ")
    .trim()
    .toLowerCase();

const normalizarServicosPrestados = (valor) => {
  const itens = Array.isArray(valor)
    ? valor
    : typeof valor === "string"
      ? valor.split(",")
      : [];

  return [
    ...new Set(
      itens
        .map((item) => {
          if (typeof item !== "string") return "";

          const servicoLimpo = item.trim();
          if (!servicoLimpo) return "";

          const equivalente = SERVICOS_DISPONIVEIS.find(
            (servico) =>
              normalizarTexto(servico) === normalizarTexto(servicoLimpo),
          );

          return equivalente || servicoLimpo;
        })
        .filter(Boolean),
    ),
  ];
};

const formatarCPF = (valor = "") => {
  const digitos = String(valor).replace(/\D/g, "").slice(0, 11);

  return digitos
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
};

const normalizarCpf = (valor = "") => String(valor).replace(/\D/g, "");

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
  const funcionarioExistente = Boolean(funcionario?.id);
  const [alterarSenha, setAlterarSenha] = useState(false);

  React.useEffect(() => {
    if (estaAberto) {
      setMostrarSenha(false);
      setAlterarSenha(false);

      if (funcionario && Object.keys(funcionario).length > 0) {
        // Garantir que servicosPrestados seja um array
        setDadosFormulario({
          ...funcionario,
          senha: "",
          servicosPrestados: normalizarServicosPrestados(
            funcionario.servicosPrestados,
          ),
          role: funcionario.role || "USER",
        });
      } else {
        setDadosFormulario({ ...formInicial });
      }
    }
  }, [estaAberto, funcionario]);

  const [mostrarSenha, setMostrarSenha] = useState(false);

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    const valorFinal = name === "cpf" ? formatarCPF(value) : value;

    setDadosFormulario({
      ...dadosFormulario,
      [name]: valorFinal,
    });
  };

  const alterarServicos = (servico) => {
    const servicosAtuais = normalizarServicosPrestados(
      dadosFormulario.servicosPrestados,
    );

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

    const cpfAtual = normalizarCpf(dadosFormulario.cpf);
    const cpfOriginal = normalizarCpf(funcionario?.cpf);
    const funcionarioExistente = Boolean(
      funcionario?.id || dadosFormulario?.id,
    );
    const cpfFoiAlterado = cpfAtual !== cpfOriginal;
    const deveValidarCpf = !funcionarioExistente || cpfFoiAlterado;

    if (deveValidarCpf && !cpfEhValido(dadosFormulario.cpf)) {
      error("CPF invalido. Digite um CPF valido.");
      return;
    }

    const shouldValidatePassword = !funcionarioExistente || alterarSenha;
    if (shouldValidatePassword) {
      const passwordValidation = validateStrongPassword(dadosFormulario.senha);
      if (!passwordValidation.isValid) {
        error(buildPasswordPolicyMessage(passwordValidation.missing));
        return;
      }
    }

    const dadosParaSalvar = {
      ...dadosFormulario,
      senha: funcionarioExistente && !alterarSenha ? "" : dadosFormulario.senha,
    };

    aoSalvar(dadosParaSalvar);
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

        <form onSubmit={enviarFormulario} autoComplete="off">
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="nomeCompleto">Nome completo</label>
              <input
                type="text"
                id="nomeCompleto"
                name="nomeCompleto"
                value={dadosFormulario.nomeCompleto}
                onChange={alterarCampo}
                autoComplete="off"
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
                maxLength={14}
                inputMode="numeric"
                placeholder="000.000.000-00"
                autoComplete="off"
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
                autoComplete="off"
                required
              />
            </div>
            <div className="grupo-formulario">
              <label>Serviços Prestados</label>
              <div className="servicos-container">
                {SERVICOS_DISPONIVEIS.map((servico) => (
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
                autoComplete="off"
                required
              />
            </div>
            <div className="grupo-formulario">
              <label htmlFor="senha">Senha</label>

              {funcionarioExistente && (
                <label
                  className="checkbox-container"
                  style={{ marginBottom: "8px" }}
                >
                  <input
                    type="checkbox"
                    checked={alterarSenha}
                    onChange={(e) => {
                      const ativo = e.target.checked;
                      setAlterarSenha(ativo);
                      if (!ativo) {
                        setMostrarSenha(false);
                        setDadosFormulario((prev) => ({ ...prev, senha: "" }));
                      }
                    }}
                  />
                  <span className="checkbox-label">
                    Redefinir senha deste funcionário
                  </span>
                </label>
              )}

              {(!funcionarioExistente || alterarSenha) && (
                <>
                  <div className="senha-container">
                    <input
                      type={mostrarSenha ? "text" : "password"}
                      id="senha"
                      name="senha"
                      value={dadosFormulario.senha}
                      onChange={alterarCampo}
                      autoComplete="new-password"
                      required={!funcionarioExistente || alterarSenha}
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
                    Minimo 8 caracteres com letra maiuscula, letra minuscula,
                    numero e caractere especial.
                  </small>
                </>
              )}
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

const ModalAlterarSenhaFuncionario = ({
  estaAberto,
  aoFechar,
  funcionario,
  aoSalvar,
}) => {
  const [novaSenha, setNovaSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [mostrarNovaSenha, setMostrarNovaSenha] = useState(false);
  const [mostrarConfirmarSenha, setMostrarConfirmarSenha] = useState(false);

  React.useEffect(() => {
    if (estaAberto) {
      setNovaSenha("");
      setConfirmarSenha("");
      setMostrarNovaSenha(false);
      setMostrarConfirmarSenha(false);
    }
  }, [estaAberto]);

  const enviarFormulario = (e) => {
    e.preventDefault();

    if (!novaSenha.trim() || !confirmarSenha.trim()) {
      error("Preencha a nova senha e a confirmacao.");
      return;
    }

    if (novaSenha !== confirmarSenha) {
      error("As senhas nao coincidem.");
      return;
    }

    const passwordValidation = validateStrongPassword(novaSenha);
    if (!passwordValidation.isValid) {
      error(buildPasswordPolicyMessage(passwordValidation.missing));
      return;
    }

    aoSalvar(novaSenha);
  };

  if (!estaAberto || !funcionario) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container modal-alterar-senha">
        <div className="modal-header">
          <h2>Alterar senha do funcionario</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviarFormulario} autoComplete="off">
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label>Funcionario</label>
              <input
                type="text"
                value={funcionario.nomeCompleto || ""}
                disabled
              />
            </div>
            <div className="grupo-formulario">
              <label>Email</label>
              <input type="text" value={funcionario.email || ""} disabled />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario grupo-senha-modal-admin">
              <label htmlFor="novaSenhaAdmin">Nova senha</label>
              <div className="senha-container">
                <input
                  type={mostrarNovaSenha ? "text" : "password"}
                  id="novaSenhaAdmin"
                  value={novaSenha}
                  onChange={(e) => setNovaSenha(e.target.value)}
                  autoComplete="new-password"
                  required
                />
                <button
                  type="button"
                  className="toggle-senha"
                  onClick={() => setMostrarNovaSenha(!mostrarNovaSenha)}
                >
                  {mostrarNovaSenha ? "👁️" : "👁️‍🗨️"}
                </button>
              </div>
            </div>
            <div className="grupo-formulario grupo-senha-modal-admin">
              <label htmlFor="confirmarSenhaAdmin">Confirmar nova senha</label>
              <div className="senha-container">
                <input
                  type={mostrarConfirmarSenha ? "text" : "password"}
                  id="confirmarSenhaAdmin"
                  value={confirmarSenha}
                  onChange={(e) => setConfirmarSenha(e.target.value)}
                  autoComplete="new-password"
                  required
                />
                <button
                  type="button"
                  className="toggle-senha"
                  onClick={() =>
                    setMostrarConfirmarSenha(!mostrarConfirmarSenha)
                  }
                >
                  {mostrarConfirmarSenha ? "👁️" : "👁️‍🗨️"}
                </button>
              </div>
            </div>
          </div>

          <small>
            Minimo 8 caracteres com letra maiuscula, letra minuscula, numero e
            caractere especial.
          </small>

          <div className="rodape-modal">
            <button type="button" className="botao-cancelar" onClick={aoFechar}>
              Cancelar
            </button>
            <button type="submit" className="botao-salvar">
              Salvar nova senha
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
  const [funcionarioParaAlterarSenha, setFuncionarioParaAlterarSenha] =
    useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalAlterarSenhaAberto, setModalAlterarSenhaAberto] = useState(false);

  useEffect(() => {
    carregarFuncionarios();
  }, []);

  const normalizarFuncionario = (f) => ({
    id: f.id ?? f.idFuncionario,
    usuarioId: f.usuarioId ?? f.idUsuario ?? null,
    nomeCompleto: f.nomeCompleto ?? f.nome ?? "",
    cpf: f.cpf ?? f.CPF ?? "",
    telefone: f.telefone ?? "",
    email: f.email ?? "",
    bio: f.bio ?? f.descricao ?? "",
    role: f.role ?? "USER",
    servicosPrestados: normalizarServicosPrestados(f.servicosPrestados ?? []),
  });

  const carregarFuncionarios = async () => {
    setCarregando(true);
    const resposta = await AuthService.getUsuarios();
    if (resposta.success) {
      setListaFuncionarios(resposta.data.map(normalizarFuncionario));
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

  const abrirAlteracaoSenha = (funcionario) => {
    setFuncionarioParaAlterarSenha({ ...funcionario });
    setModalAlterarSenhaAberto(true);
  };

  const alterarSenhaFuncionario = async (novaSenha) => {
    if (!funcionarioParaAlterarSenha?.id) {
      error("Funcionario invalido para alteracao de senha.");
      return;
    }

    try {
      const resposta = await AuthService.resetarSenhaAdmin(
        funcionarioParaAlterarSenha,
        novaSenha,
      );

      if (resposta.success) {
        success("Senha do funcionario alterada com sucesso!");
        setModalAlterarSenhaAberto(false);
        setFuncionarioParaAlterarSenha(null);
        return;
      }

      error(resposta.error || "Erro ao alterar senha do funcionario");
    } catch (err) {
      console.error("Erro ao alterar senha do funcionario:", err);
      error("Erro ao alterar senha do funcionario");
    }
  };

  const prepararExclusao = (funcionario) => {
    setFuncionarioParaExcluir(funcionario);
    setModalConfirmacaoExclusaoAberto(true);
  };

  const confirmarExclusao = async () => {
    if (funcionarioParaExcluir?.id) {
      const resposta = await AuthService.deleteUsuario(funcionarioParaExcluir);

      if (resposta.success) {
        setListaFuncionarios(
          listaFuncionarios.filter(
            (funcionario) => funcionario.id !== funcionarioParaExcluir.id,
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
        const cpfAtual = normalizarCpf(dadosFuncionario.cpf);
        const cpfOriginal = normalizarCpf(funcionarioEmEdicao?.cpf);
        const cpfFoiAlterado = cpfAtual !== cpfOriginal;

        const dadosAtualizacao = {
          ...dadosFuncionario,
          usuarioId:
            dadosFuncionario.usuarioId ?? funcionarioEmEdicao?.usuarioId,
          cpfOriginal: funcionarioEmEdicao?.cpf,
          emailOriginal: funcionarioEmEdicao?.email,
          senha: "",
          email: String(dadosFuncionario.email || "")
            .trim()
            .toLowerCase(),
          servicosPrestados: normalizarServicosPrestados(
            dadosFuncionario.servicosPrestados,
          ),
        };

        if (cpfFoiAlterado) {
          dadosAtualizacao.cpf = cpfAtual;
        } else {
          delete dadosAtualizacao.cpf;
        }

        dadosAtualizacao.enviarCpf = cpfFoiAlterado;

        // EDITAR
        // authService.updateUsuario faz o mapeamento para nome/CPF/descricao
        resposta = await AuthService.updateUsuario(
          dadosFuncionario.id,
          dadosAtualizacao,
        );

        if (resposta.success) {
          // Atualizar lista local
          setListaFuncionarios(
            listaFuncionarios.map((func) =>
              func.id === dadosFuncionario.id
                ? {
                    ...func,
                    ...dadosFuncionario,
                    cpf: cpfFoiAlterado ? formatarCPF(cpfAtual) : func.cpf,
                  }
                : func,
            ),
          );
          success("Funcionário atualizado com sucesso!");
        } else {
          error(resposta.error || "Erro ao atualizar funcionário");
        }
      } else {
        // CRIAR
        resposta = await AuthService.register({
          nomeCompleto: dadosFuncionario.nomeCompleto,
          cpf: normalizarCpf(dadosFuncionario.cpf),
          telefone: dadosFuncionario.telefone,
          servicosPrestados: normalizarServicosPrestados(
            dadosFuncionario.servicosPrestados,
          ),
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
          error(resposta.error || "Erro ao registrar funcionário");
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
                      className="botao-tabela-senha"
                      onClick={() => abrirAlteracaoSenha(funcionario)}
                    >
                      Alterar senha
                    </button>
                    <button
                      className="botao-tabela-excluir"
                      onClick={() => prepararExclusao(funcionario)}
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

      <ModalAlterarSenhaFuncionario
        estaAberto={modalAlterarSenhaAberto}
        aoFechar={() => {
          setModalAlterarSenhaAberto(false);
          setFuncionarioParaAlterarSenha(null);
        }}
        funcionario={funcionarioParaAlterarSenha}
        aoSalvar={alterarSenhaFuncionario}
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
