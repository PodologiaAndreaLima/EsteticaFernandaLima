import React, { useState } from "react";
import "./Perfil.css";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";

// Componente Modal para Edição de Perfil
const ModalEditarPerfil = ({ estaAberto, aoFechar, perfil, aoSalvar }) => {
  const [dadosFormulario, setDadosFormulario] = useState(perfil);

  // Lista de serviços disponíveis
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
    setDadosFormulario({
      ...dadosFormulario,
      [name]: value,
    });
  };

  const alterarServicos = (servico) => {
    const servicosAtuais = [...dadosFormulario.servicosPrestados];
    const index = servicosAtuais.indexOf(servico);

    if (index > -1) {
      // Remove se já estiver selecionado
      servicosAtuais.splice(index, 1);
    } else {
      // Adiciona se não estiver selecionado
      servicosAtuais.push(servico);
    }

    setDadosFormulario({
      ...dadosFormulario,
      servicosPrestados: servicosAtuais,
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
                type="text"
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
                      checked={dadosFormulario.servicosPrestados.includes(
                        servico
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

// Componente Modal para Alterar Senha
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
    aoSalvar({ senhaAtual, novaSenha });
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
  // Estado para armazenar os dados do perfil
  const [dadosPerfil, setDadosPerfil] = useState({
    nomeCompleto: "Fulano da Silva",
    cpf: "123.456.789-00",
    email: "fulano@gmail.com",
    telefone: "(00) 90000-0000",
    bio: "Gosto de estética, comecei em 2016...",
    servicosPrestados: ["Micropigmentação", "Peeling Facial"],
  });

  // Estados para os modais
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [modalSenhaAberto, setModalSenhaAberto] = useState(false);
  const [modalExcluirAberto, setModalExcluirAberto] = useState(false);

  const salvarPerfil = (novosDados) => {
    setDadosPerfil(novosDados);
    console.log("Perfil salvo:", novosDados);
  };

  const alterarSenha = (dadosSenha) => {
    console.log("Senha alterada");
  };

  const excluirConta = () => {
    console.log("Conta excluída");
    setModalExcluirAberto(false);
    window.location.href = "/";
  };

  return (
    <div className="container-perfil">
      <h1>Perfil</h1>

      {/* Card com informações do perfil */}
      <div className="card-perfil">
        <div className="card-header-perfil">
          <div className="info-usuario">
            <div className="avatar-perfil">
              <span>{dadosPerfil.nomeCompleto.charAt(0)}</span>
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
              <span className="info-valor">{dadosPerfil.cpf}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Telefone:</span>
              <span className="info-valor">{dadosPerfil.telefone}</span>
            </div>
            <div className="info-item info-item-full">
              <span className="info-label">Bio:</span>
              <span className="info-valor">{dadosPerfil.bio}</span>
            </div>
            <div className="info-item info-item-full">
              <span className="info-label">Serviços Prestados:</span>
              <span className="info-valor">
                {dadosPerfil.servicosPrestados.join(", ")}
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

      {/* Modal para editar perfil */}
      <ModalEditarPerfil
        estaAberto={modalEditarAberto}
        aoFechar={() => setModalEditarAberto(false)}
        perfil={dadosPerfil}
        aoSalvar={salvarPerfil}
      />

      {/* Modal para alterar senha */}
      <ModalAlterarSenha
        estaAberto={modalSenhaAberto}
        aoFechar={() => setModalSenhaAberto(false)}
        aoSalvar={alterarSenha}
      />

      {/* Modal de confirmação de exclusão */}
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
