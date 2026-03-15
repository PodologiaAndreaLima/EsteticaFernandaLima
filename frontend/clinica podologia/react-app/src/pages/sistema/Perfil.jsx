import React, { useEffect, useState } from "react";
import "./Perfil.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import { useAuth } from "../../contexts/AuthContext";
import perfilService from "../../services/perfilService";
import { success, error as toastError } from "../../services/toastService";
import { useNavigate } from "react-router-dom";
import {
  buildPasswordPolicyMessage,
  getFriendlyPasswordError,
  validateStrongPassword,
} from "../../utils/authErrorUtils";

const ModalEditarPerfil = ({ estaAberto, aoFechar, perfil, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState({
    nomeCompleto: "",
    cpf: "",
    email: "",
    telefone: "",
    bio: "",
    servicosPrestados: [],
  });

  // quando abrir, inicializa com o perfil atual
  useEffect(() => {
    if (estaAberto) {
      setDadosFormulario({
        nomeCompleto: perfil?.nomeCompleto ?? "",
        cpf: perfil?.cpf ?? "",
        email: perfil?.email ?? "",
        telefone: perfil?.telefone ?? "",
        bio: perfil?.bio ?? "",
        servicosPrestados: Array.isArray(perfil?.servicosPrestados)
          ? perfil.servicosPrestados
          : [],
      });
    }
  }, [estaAberto, perfil]);

  const servicosDisponiveis = [
    "Micropigmentação",
    "Peeling Facial",
    "Limpeza de Pele",
    "Massagem Relaxante",
    "Depilação",
    "Design de Sobrancelhas",
  ];

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    setDadosFormulario((prev) => ({ ...prev, [name]: value }));
  };

  const alterarServicos = (servico) => {
    setDadosFormulario((prev) => {
      const copia = [...(prev.servicosPrestados || [])];
      const idx = copia.indexOf(servico);
      if (idx > -1) copia.splice(idx, 1);
      else copia.push(servico);
      return { ...prev, servicosPrestados: copia };
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
          <h2>Editar Perfil</h2>
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
                type="text"
                id="telefone"
                name="telefone"
                value={dadosFormulario.telefone}
                onChange={alterarCampo}
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label htmlFor="bio">Bio:</label>
              <textarea
                id="bio"
                name="bio"
                value={dadosFormulario.bio}
                onChange={alterarCampo}
                rows="3"
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label>Serviços Prestados</label>
              <div className="grupo-checkboxes">
                {servicosDisponiveis.map((servico) => (
                  <label key={servico} className="checkbox-item">
                    <input
                      type="checkbox"
                      value={servico}
                      checked={dadosFormulario.servicosPrestados?.includes(
                        servico,
                      )}
                      onChange={() => alterarServicos(servico)}
                    />
                    <span>{servico}</span>
                  </label>
                ))}
              </div>
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

const ModalAlterarSenha = ({ estaAberto, aoFechar, aoSalvar }) => {
  const [senhaAtual, setSenhaAtual] = useState("");
  const [novaSenha, setNovaSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [mostrarSenhaAtual, setMostrarSenhaAtual] = useState(false);
  const [mostrarNovaSenha, setMostrarNovaSenha] = useState(false);
  const [mostrarConfirmarSenha, setMostrarConfirmarSenha] = useState(false);

  const enviarFormulario = (e) => {
    e.preventDefault();
    if (novaSenha !== confirmarSenha) {
      alert("As senhas não coincidem!");
      return;
    }

    const passwordValidation = validateStrongPassword(novaSenha);
    if (!passwordValidation.isValid) {
      alert(buildPasswordPolicyMessage(passwordValidation.missing));
      return;
    }

    aoSalvar({ senhaAtual, novaSenha });
    // reseta campos
    setSenhaAtual("");
    setNovaSenha("");
    setConfirmarSenha("");
    aoFechar();
  };

  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>Alterar Senha</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviarFormulario}>
          <div className="linha-formulario">
            <div className="grupo-formulario grupo-senha">
              <label htmlFor="senhaAtual">Senha Atual</label>
              <div className="input-senha">
                <input
                  type={mostrarSenhaAtual ? "text" : "password"}
                  id="senhaAtual"
                  value={senhaAtual}
                  onChange={(e) => setSenhaAtual(e.target.value)}
                  placeholder="••••••••"
                  required
                />
                <button
                  type="button"
                  className="botao-ver-senha"
                  onClick={() => setMostrarSenhaAtual(!mostrarSenhaAtual)}
                >
                  {mostrarSenhaAtual ? "👁️" : "👁️‍🗨️"}
                </button>
              </div>
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario grupo-senha">
              <label htmlFor="novaSenha">Nova Senha</label>
              <div className="input-senha">
                <input
                  type={mostrarNovaSenha ? "text" : "password"}
                  id="novaSenha"
                  value={novaSenha}
                  onChange={(e) => setNovaSenha(e.target.value)}
                  placeholder="••••••••"
                  required
                />
                <button
                  type="button"
                  className="botao-ver-senha"
                  onClick={() => setMostrarNovaSenha(!mostrarNovaSenha)}
                >
                  {mostrarNovaSenha ? "👁️" : "👁️‍🗨️"}
                </button>
              </div>
              <small>
                Minimo 8 caracteres com letra maiuscula, letra minuscula, numero
                e caractere especial.
              </small>
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario grupo-senha">
              <label htmlFor="confirmarSenha">Confirmar Nova Senha</label>
              <div className="input-senha">
                <input
                  type={mostrarConfirmarSenha ? "text" : "password"}
                  id="confirmarSenha"
                  value={confirmarSenha}
                  onChange={(e) => setConfirmarSenha(e.target.value)}
                  placeholder="••••••••"
                  required
                />
                <button
                  type="button"
                  className="botao-ver-senha"
                  onClick={() =>
                    setMostrarConfirmarSenha(!mostrarConfirmarSenha)
                  }
                >
                  {mostrarConfirmarSenha ? "👁️" : "👁️‍🗨️"}
                </button>
              </div>
            </div>
          </div>

          <div className="rodape-modal">
            <button type="button" className="botao-cancelar" onClick={aoFechar}>
              Cancelar
            </button>
            <button type="submit" className="botao-salvar">
              Alterar Senha
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const Perfil = () => {
  const { user, setUser } = useAuth();
  const navigate = useNavigate();

  const [dadosPerfil, setDadosPerfil] = useState(null);
  const [carregando, setCarregando] = useState(true);

  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalSenhaAberto, setModalSenhaAberto] = useState(false);
  const [modalExcluirAberto, setModalExcluirAberto] = useState(false);

  useEffect(() => {
    const id = user?.id ?? localStorage.getItem("userId");
    if (!id) {
      // Sem id — redireciona para login ou mostra aviso
      console.warn("ID do usuário não disponível no contexto/localStorage.");
      setCarregando(false);
      return;
    }
    fetchPerfil(Number(id));
  }, [user]);

  const fetchPerfil = async (id) => {
    setCarregando(true);
    try {
      const res = await perfilService.getById(id);
      // se o service retornar o DTO compatível com UsuarioListarDto
      setDadosPerfil({
        id: res.id,
        nomeCompleto: res.nomeCompleto ?? res.nome ?? "",
        cpf: res.cpf ?? "",
        email: res.email ?? "",
        telefone: res.telefone ?? "",
        bio: res.bio ?? "",
        servicosPrestados: Array.isArray(res.servicosPrestados)
          ? res.servicosPrestados
          : [],
        role: res.role ?? user?.role ?? null,
      });
    } catch (err) {
      console.error("Erro ao buscar perfil:", err);
      toastError("Erro ao carregar perfil");
    } finally {
      setCarregando(false);
    }
  };

  const salvarPerfil = async (novosDados) => {
    const id = user?.id ?? localStorage.getItem("userId");
    if (!id) {
      alert("ID do usuário não disponível para salvar o perfil.");
      return;
    }
    try {
      // payload deve seguir UsuarioCriacaoDto (back usa esse DTO no PUT)
      const payload = {
        nomeCompleto: novosDados.nomeCompleto,
        cpf: novosDados.cpf,
        telefone: novosDados.telefone,
        bio: novosDados.bio,
        servicosPrestados: novosDados.servicosPrestados,
        email: novosDados.email,
      };
      const atualizado = await perfilService.update(Number(id), payload);
      success("Perfil atualizado com sucesso!");
      // atualizar estado local e localStorage
      setDadosPerfil((prev) => ({ ...prev, ...novosDados }));
      try {
        // atualiza user no localStorage (se existir)
        const stored = localStorage.getItem("user");
        if (stored) {
          const parsed = JSON.parse(stored);
          parsed.nomeCompleto =
            atualizado.nomeCompleto ?? atualizado.nome ?? parsed.nomeCompleto;
          parsed.email = atualizado.email ?? parsed.email;
          localStorage.setItem("user", JSON.stringify(parsed));
        }
        // atualiza também campos separados (userName)
        if (atualizado.nomeCompleto)
          localStorage.setItem("userName", atualizado.nomeCompleto);
        if (atualizado.email)
          localStorage.setItem("userEmail", atualizado.email);
      } catch (e) {}
      if (setUser) {
        setUser((u) => ({
          ...(u || {}),
          nome: atualizado.nomeCompleto ?? atualizado.nome ?? u?.nome,
          email: atualizado.email ?? u?.email,
        }));
      }
    } catch (err) {
      console.error("Erro ao salvar perfil:", err);
      toastError("Erro ao salvar perfil");
    }
  };

  const alterarSenha = async ({ senhaAtual, novaSenha }) => {
    const id = user?.id ?? localStorage.getItem("userId");
    if (!id) {
      alert("ID do usuário não disponível para alterar senha.");
      return;
    }
    try {
      // payload conforme seu DTO TrocaSenhaDto
      const payload = { senhaAtual, novaSenha };

      await perfilService.changePassword(Number(id), payload);
      success("Senha alterada com sucesso!");
    } catch (err) {
      console.error("Erro ao alterar senha:", err);
      const msg = getFriendlyPasswordError(err, "Erro ao alterar senha");
      toastError(String(msg));
    }
  };

  const excluirConta = () => {
    console.log("Conta excluída (ainda só mock).");
    setModalExcluirAberto(false);
    // logout / redirecionar
    localStorage.clear();
    navigate("/", { replace: true });
  };

  if (carregando) {
    return (
      <div className="container-perfil">
        <p>Carregando perfil...</p>
      </div>
    );
  }

  if (!dadosPerfil) {
    return (
      <div className="container-perfil">
        <h1>Perfil</h1>
        <p>Não foi possível carregar os dados do perfil.</p>
      </div>
    );
  }

  return (
    <div className="container-perfil">
      <h1>Perfil</h1>

      <div className="card-perfil">
        <div className="card-header-perfil">
          <div className="info-usuario">
            <div className="avatar-perfil">
              <span>{(dadosPerfil.nomeCompleto || "U").charAt(0)}</span>
            </div>
            <div className="dados-usuario">
              <h2>{dadosPerfil.nomeCompleto}</h2>
              <p>{dadosPerfil.email}</p>
            </div>
          </div>
        </div>

        <div className="card-body-perfil">
          <div className="info-grid">
            <div className="info-item">
              <span className="info-label">CPF:</span>
              <span className="info-valor">{dadosPerfil.cpf || "-"}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Telefone:</span>
              <span className="info-valor">{dadosPerfil.telefone || "-"}</span>
            </div>
            <div className="info-item info-item-full">
              <span className="info-label">Bio:</span>
              <span className="info-valor">{dadosPerfil.bio || "-"}</span>
            </div>
            <div className="info-item info-item-full">
              <span className="info-label">Serviços Prestados:</span>
              <span className="info-valor">
                {Array.isArray(dadosPerfil.servicosPrestados) &&
                dadosPerfil.servicosPrestados.length > 0
                  ? dadosPerfil.servicosPrestados.join(", ")
                  : "-"}
              </span>
            </div>
          </div>
        </div>

        <div className="card-actions-perfil">
          <button
            className="botao-acao botao-editar"
            onClick={() => setModalEditarAberto(true)}
          >
            Editar Perfil
          </button>
          <button
            className="botao-acao botao-senha"
            onClick={() => setModalSenhaAberto(true)}
          >
            Alterar Senha
          </button>
          <button
            className="botao-acao botao-excluir"
            onClick={() => setModalExcluirAberto(true)}
          >
            Excluir Conta
          </button>
        </div>
      </div>

      {/* Modais */}
      <ModalEditarPerfil
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        perfil={dadosPerfil}
        aoSalvar={salvarPerfil}
      />

      <ModalAlterarSenha
        estaAberto={modalSenhaAberto}
        aoFechar={() => setModalSenhaAberto(false)}
        aoSalvar={alterarSenha}
      />

      <ModalConfirmacao
        estaAberto={modalExcluirAberto}
        aoFechar={() => setModalExcluirAberto(false)}
        aoConfirmar={excluirConta}
        titulo="Confirmar exclusão de conta"
        mensagem="Tem certeza que deseja excluir sua conta? Todos os seus dados serão permanentemente removidos e esta ação não pode ser desfeita."
        textoBotaoConfirmar="Excluir Conta"
        textoBotaoCancelar="Cancelar"
        tipo="exclusao"
      />
    </div>
  );
};

export default Perfil;
