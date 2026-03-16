import api from "./api";
import API_CONFIG from "../config/apiConfig";
import Cliente from "../models/Cliente";
import Funcionario from "../models/Funcionario";
import {
  extractApiErrorMessage,
  getFriendlyLoginError,
  getFriendlyPasswordError,
} from "../utils/authErrorUtils";

const SERVICOS_CACHE_KEY = "funcionarios_servicos_por_email";

const getServicosCache = () => {
  try {
    const raw = localStorage.getItem(SERVICOS_CACHE_KEY);
    const parsed = raw ? JSON.parse(raw) : {};
    return parsed && typeof parsed === "object" ? parsed : {};
  } catch {
    return {};
  }
};

const salvarServicosNoCache = (email, servicosPrestados = []) => {
  const emailKey = String(email || "")
    .trim()
    .toLowerCase();
  if (!emailKey) return;

  const lista = Array.isArray(servicosPrestados)
    ? servicosPrestados.filter((s) => typeof s === "string" && s.trim())
    : [];

  const cache = getServicosCache();
  cache[emailKey] = [...new Set(lista)];
  localStorage.setItem(SERVICOS_CACHE_KEY, JSON.stringify(cache));
};

export const AuthService = {
  // Função para fazer login
  login: async (identifier, senha) => {
    try {
      const normalizedEmail = String(identifier || "")
        .trim()
        .toLowerCase();

      const response = await api.post("/usuarios/login", {
        email: normalizedEmail,
        senha: senha,
      });

      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        localStorage.setItem(
          "user",
          JSON.stringify({
            id: response.data.userId,
            nome: response.data.nomeCompleto,
            email: response.data.email,
            role: response.data.role,
          }),
        );
      }

      return {
        success: true,
        user: response.data,
        token: response.data.token,
      };
    } catch (error) {
      return {
        success: false,
        error: getFriendlyLoginError(error),
      };
    }
  },

  // Função para registrar novo funcionário
  register: async (dados) => {
    try {
      const normalizedEmail = String(dados.email || "")
        .trim()
        .toLowerCase();

      salvarServicosNoCache(normalizedEmail, dados.servicosPrestados);

      // 1) Cria no módulo de usuários para permitir autenticação em /usuarios/login
      await api.post("/usuarios", {
        nomeCompleto: dados.nomeCompleto,
        cpf: dados.cpf,
        telefone: dados.telefone,
        bio: dados.bio,
        servicosPrestados: dados.servicosPrestados,
        email: normalizedEmail,
        senha: dados.senha,
        role: dados.role,
      });

      // 2) Cria no módulo de funcionários para aparecer na gestão de funcionários
      await api.post("/funcionarios", {
        nome: dados.nomeCompleto,
        CPF: dados.cpf,
        cpf: dados.cpf,
        descricao: dados.bio,
        email: normalizedEmail,
        senha: dados.senha,
        telefone: dados.telefone,
      });

      return {
        success: true,
        message: "Funcionário cadastrado com sucesso!",
      };
    } catch (error) {
      return {
        success: false,
        error: getFriendlyPasswordError(error, "Erro ao cadastrar funcionário"),
      };
    }
  },

  // Buscar todos os funcionários
  getUsuarios: async () => {
    try {
      // Junta dados de /funcionarios (gestao) + /usuarios (servicosPrestados/role)
      const [responseFuncionarios, responseUsuarios] = await Promise.all([
        api.get("/funcionarios"),
        api.get("/usuarios"),
      ]);

      const funcionarios = Array.isArray(responseFuncionarios.data)
        ? responseFuncionarios.data
        : [];
      const usuarios = Array.isArray(responseUsuarios.data)
        ? responseUsuarios.data
        : [];

      const usuariosPorEmail = new Map(
        usuarios
          .filter((u) => u?.email)
          .map((u) => [String(u.email).toLowerCase(), u]),
      );
      const servicosCache = getServicosCache();

      const dataMesclada = funcionarios.map((f) => {
        const email = String(f?.email || "").toLowerCase();
        const usuario = usuariosPorEmail.get(email) || {};
        const servicosDoCache = servicosCache[email] || [];

        return {
          ...f,
          usuarioId: usuario?.id ?? usuario?.idUsuario ?? null,
          nomeCompleto: f?.nomeCompleto ?? f?.nome ?? usuario?.nomeCompleto,
          cpf: f?.cpf ?? f?.CPF ?? usuario?.cpf,
          bio: f?.bio ?? f?.descricao ?? usuario?.bio,
          servicosPrestados:
            f?.servicosPrestados ??
            usuario?.servicosPrestados ??
            servicosDoCache,
          role: f?.role ?? usuario?.role,
        };
      });

      return {
        success: true,
        data: dataMesclada,
      };
    } catch (error) {
      return {
        success: false,
        error: extractApiErrorMessage(error) || "Erro ao buscar funcionários",
      };
    }
  },

  // Atualizar funcionário
  updateUsuario: async (usuarioId, dados) => {
    try {
      salvarServicosNoCache(dados.email, dados.servicosPrestados);

      const emailNormalizado = String(dados.email || "")
        .trim()
        .toLowerCase();

      const cpfNormalizado = String(dados.cpf ?? dados.CPF ?? "").replace(
        /\D/g,
        "",
      );
      const deveEnviarCpf = dados.enviarCpf === true;

      // PUT /funcionarios/{id} — contrato do DTO de atualização
      const payload = {
        nome: dados.nomeCompleto ?? dados.nome,
        descricao: dados.bio ?? dados.descricao,
        email: dados.email,
        telefone: dados.telefone,
      };

      if (deveEnviarCpf && cpfNormalizado) {
        payload.CPF = cpfNormalizado;
      }

      if (dados.senha && dados.senha.trim()) {
        payload.senha = dados.senha;
      }

      const response = await api.put(`/funcionarios/${usuarioId}`, payload);

      // Sincroniza com /usuarios apenas quando ha troca de senha.
      if (dados.senha && dados.senha.trim()) {
        let usuarioSistemaId =
          dados.usuarioId ?? dados.userId ?? dados.idUsuario ?? null;

        const emailOriginalNormalizado = String(dados.emailOriginal || "")
          .trim()
          .toLowerCase();

        if (
          !usuarioSistemaId &&
          (emailNormalizado || emailOriginalNormalizado)
        ) {
          try {
            const usuariosResponse = await api.get("/usuarios");
            const usuarios = Array.isArray(usuariosResponse.data)
              ? usuariosResponse.data
              : [];
            const usuarioEncontrado = usuarios.find((u) =>
              [emailNormalizado, emailOriginalNormalizado]
                .filter(Boolean)
                .includes(
                  String(u?.email || "")
                    .trim()
                    .toLowerCase(),
                ),
            );
            usuarioSistemaId =
              usuarioEncontrado?.id ?? usuarioEncontrado?.idUsuario ?? null;
          } catch {
            usuarioSistemaId = null;
          }
        }

        if (!usuarioSistemaId) {
          throw new Error(
            "Nao foi possivel localizar o usuario de login para sincronizar a nova senha.",
          );
        }

        let senhaSincronizada = false;
        const cpfSync = String(
          dados.cpf ?? dados.CPF ?? dados.cpfOriginal ?? "",
        ).replace(/\D/g, "");
        const payloadUsuarioCompleto = {
          nomeCompleto: dados.nomeCompleto ?? dados.nome ?? "",
          cpf: cpfSync,
          telefone: dados.telefone ?? "",
          bio: dados.bio ?? dados.descricao ?? "",
          servicosPrestados: Array.isArray(dados.servicosPrestados)
            ? dados.servicosPrestados
            : [],
          email: emailNormalizado || emailOriginalNormalizado || "",
          senha: dados.senha,
          role: dados.role ?? "USER",
        };
        const tentativas = [
          () =>
            api.put(`/usuarios/${usuarioSistemaId}/senha`, {
              senha: dados.senha,
            }),
          () =>
            api.put(`/usuarios/${usuarioSistemaId}/password`, {
              senha: dados.senha,
            }),
          () =>
            api.put(`/usuarios/${usuarioSistemaId}`, payloadUsuarioCompleto),
        ];

        let ultimoErro = null;
        for (const tentar of tentativas) {
          try {
            await tentar();
            senhaSincronizada = true;
            break;
          } catch (err) {
            ultimoErro = err;
          }
        }

        if (!senhaSincronizada && ultimoErro) {
          throw ultimoErro;
        }
      }

      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      const status = error?.response?.status;
      const data = error?.response?.data;
      const url = error?.config?.url;
      const method = String(error?.config?.method || "").toUpperCase();

      // Log temporario de diagnostico para identificar o endpoint/validacao que falhou.
      console.error("[AuthService.updateUsuario] Falha na requisicao", {
        usuarioId,
        endpoint: url,
        method,
        status,
        responseData: data,
      });

      const detailedFallback =
        extractApiErrorMessage(error) ||
        error?.message ||
        "Erro ao atualizar funcionário";

      return {
        success: false,
        error: getFriendlyPasswordError(error, detailedFallback),
      };
    }
  },

  // Deletar funcionário
  deleteUsuario: async (usuarioId) => {
    try {
      await api.delete(`/funcionarios/${usuarioId}`);
      return {
        success: true,
        message: "Funcionário deletado com sucesso",
      };
    } catch (error) {
      return {
        success: false,
        error: extractApiErrorMessage(error) || "Erro ao deletar funcionario",
      };
    }
  },

  // Função para obter os dados do funcionário logado
  // Como o backend não tem um endpoint /funcionario/me, usamos o ID armazenado localmente
  getCurrentUser: async () => {
    try {
      // Obtém o usuário do localStorage
      const localUser = AuthService.getLocalUser();

      if (!localUser || !localUser.id) {
        return { success: false, error: "Funcionário não encontrado" };
      }

      // Busca os dados atualizados do funcionário
      const response = await api.get(
        `${API_CONFIG.ENDPOINTS.STAFF.BASE}/${localUser.id}`,
      );

      if (response.data) {
        // Atualiza o localStorage com os dados mais recentes
        const funcionario = Funcionario.fromAPI(response.data);
        localStorage.setItem("user", JSON.stringify(funcionario));

        return {
          success: true,
          user: funcionario,
        };
      }

      return { success: false, error: "Erro ao buscar dados do usuário" };
    } catch (error) {
      console.error("Erro ao buscar usuário:", error);
      return { success: false, error: "Erro ao buscar dados do usuário" };
    }
  },

  // Função para atualizar os dados do funcionário
  updateUser: async (userId, userData) => {
    try {
      // Convertemos para o formato esperado pelo backend
      const funcionarioData = new Funcionario(userData).toAPI();

      // Fazemos a chamada para atualizar o funcionário
      const response = await api.put(
        `${API_CONFIG.ENDPOINTS.STAFF.BASE}/${userId}`,
        funcionarioData,
      );

      if (response.data) {
        // Atualiza o localStorage com os dados atualizados
        const funcionarioAtualizado = Funcionario.fromAPI(response.data);
        localStorage.setItem("user", JSON.stringify(funcionarioAtualizado));

        return {
          success: true,
          user: funcionarioAtualizado,
        };
      }

      return { success: false, error: "Erro ao atualizar dados" };
    } catch (error) {
      console.error("Erro ao atualizar usuário:", error);
      const errorMessage =
        extractApiErrorMessage(error) || "Erro ao atualizar dados";
      return { success: false, error: errorMessage };
    }
  },

  // Função para fazer logout
  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  },

  // Função para obter o usuário atual do localStorage
  getLocalUser: () => {
    const user = localStorage.getItem("user");
    return user ? JSON.parse(user) : null;
  },
};
