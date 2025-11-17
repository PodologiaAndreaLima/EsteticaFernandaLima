import React, { useState, useEffect } from "react";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import { success } from "../../services/toastService";
import CatalogService from "../../services/catalogService";
import "./OrdemServico.css";

const ModalOrdem = ({
  estaAberto,
  aoFechar,
  ordemInicial,
  aoSalvar,
  listaServicos,
  listaProdutos,
}) => {
  const [ordem, setOrdem] = useState({
    cliente: "",
    funcionario: "",
    servicos: [],
    produtos: [],
    quantidade: 1,
    valorTotal: "0,00",
    observacoes: "",
  });

  useEffect(() => {
    if (estaAberto) {
      setOrdem(
        ordemInicial || {
          cliente: "",
          funcionario: "",
          servicos: [],
          produtos: [],
          quantidade: 1,
          valorTotal: "0,00",
          observacoes: "",
        }
      );
    }
  }, [estaAberto, ordemInicial]);

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    setOrdem({ ...ordem, [name]: value });
  };

  const adicionarServico = () => {
    setOrdem({
      ...ordem,
      servicos: [...ordem.servicos, { servico: "", desconto: 0 }],
    });
  };

  const removerServico = (idx) => {
    const servicos = ordem.servicos.filter((_, i) => i !== idx);
    setOrdem({ ...ordem, servicos });
  };

  const alterarServicoLinha = (idx, campo, valor) => {
    const servicos = ordem.servicos.map((s, i) =>
      i === idx ? { ...s, [campo]: valor } : s
    );
    setOrdem({ ...ordem, servicos });
  };

  const adicionarProduto = () => {
    setOrdem({
      ...ordem,
      produtos: [
        ...ordem.produtos,
        { produto: "", quantidade: 1, desconto: 0 },
      ],
    });
  };

  const removerProduto = (idx) => {
    const produtos = ordem.produtos.filter((_, i) => i !== idx);
    setOrdem({ ...ordem, produtos });
  };

  const enviar = (e) => {
    e.preventDefault();
    aoSalvar(ordem);
    aoFechar();
  };

  if (!estaAberto) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container ordem-modal">
        <div className="modal-header">
          <h2>{ordem.id ? "Editar Ordem" : "Nova Ordem"}</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <form onSubmit={enviar} className="ordem-form">
          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label>Cliente</label>
              <input
                name="cliente"
                value={ordem.cliente}
                onChange={alterarCampo}
              />
            </div>
            <div className="grupo-formulario">
              <label>Funcionário</label>
              <input
                name="funcionario"
                value={ordem.funcionario}
                onChange={alterarCampo}
              />
            </div>
          </div>

          <div className="linha-formulario">
            <button
              type="button"
              className="botao-adicionar botao-adicionar-grande"
              onClick={adicionarServico}
            >
              + Adicionar Serviço
            </button>
          </div>

          {ordem.servicos.map((s, idx) => (
            <div key={idx} className="linha-formulario pequena">
              <div className="grupo-formulario">
                <label>Serviço / Combo</label>
                <select
                  value={s.servico}
                  onChange={(e) =>
                    alterarServicoLinha(idx, "servico", e.target.value)
                  }
                >
                  <option value="">-- selecione --</option>
                  {listaServicos.map((sv) => (
                    <option key={sv.id} value={sv.nome}>
                      {sv.nome}
                    </option>
                  ))}
                </select>
              </div>
              <div className="grupo-formulario small">
                <label>Desconto (%)</label>
                <input
                  type="number"
                  min="0"
                  value={s.desconto}
                  onChange={(e) =>
                    alterarServicoLinha(idx, "desconto", e.target.value)
                  }
                />
              </div>
              <div className="grupo-formulario tiny">
                <button
                  type="button"
                  className="botao-excluir-pequeno"
                  onClick={() => removerServico(idx)}
                  aria-label="Remover serviço"
                >
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                    aria-hidden
                  >
                    <path
                      d="M3 6h18"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                    <path
                      d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                    <path
                      d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                    <path
                      d="M10 11v6M14 11v6"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                  </svg>
                </button>
              </div>
            </div>
          ))}

          <div className="linha-formulario">
            <button
              type="button"
              className="botao-adicionar botao-adicionar-grande"
              onClick={adicionarProduto}
            >
              + Adicionar Produto
            </button>
          </div>

          {ordem.produtos.map((p, idx) => (
            <div key={idx} className="linha-formulario pequena">
              <div className="grupo-formulario">
                <label>Produto</label>
                <select
                  value={p.produto}
                  onChange={(e) => {
                    const produtos = ordem.produtos.map((pr, i) =>
                      i === idx ? { ...pr, produto: e.target.value } : pr
                    );
                    setOrdem({ ...ordem, produtos });
                  }}
                >
                  <option value="">-- selecione --</option>
                  {listaProdutos.map((pd) => (
                    <option key={pd.id} value={pd.nome}>
                      {pd.nome}
                    </option>
                  ))}
                </select>
              </div>
              <div className="grupo-formulario small">
                <label>Quantidade</label>
                <input
                  type="number"
                  min="1"
                  value={p.quantidade}
                  onChange={(e) => {
                    const produtos = ordem.produtos.map((pr, i) =>
                      i === idx ? { ...pr, quantidade: e.target.value } : pr
                    );
                    setOrdem({ ...ordem, produtos });
                  }}
                />
              </div>
              <div className="grupo-formulario small">
                <label>Desconto (%)</label>
                <input
                  type="number"
                  min="0"
                  value={p.desconto}
                  onChange={(e) => {
                    const produtos = ordem.produtos.map((pr, i) =>
                      i === idx ? { ...pr, desconto: e.target.value } : pr
                    );
                    setOrdem({ ...ordem, produtos });
                  }}
                />
              </div>
              <div className="grupo-formulario tiny">
                <button
                  type="button"
                  className="botao-excluir-pequeno"
                  onClick={() => removerProduto(idx)}
                  aria-label="Remover produto"
                >
                  <svg
                    width="16"
                    height="16"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                    aria-hidden
                  >
                    <path
                      d="M3 6h18"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                    <path
                      d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                    <path
                      d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                    <path
                      d="M10 11v6M14 11v6"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                  </svg>
                </button>
              </div>
            </div>
          ))}

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label>Quantidade</label>
              <input
                value={ordem.quantidade}
                name="quantidade"
                onChange={alterarCampo}
              />
            </div>
            <div className="grupo-formulario">
              <label>Valor total (R$)</label>
              <input
                value={ordem.valorTotal}
                name="valorTotal"
                onChange={alterarCampo}
              />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario full">
              <label>Observações</label>
              <textarea
                value={ordem.observacoes}
                name="observacoes"
                onChange={alterarCampo}
                rows={4}
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

// Modal de visualização da Ordem
const ModalVisualizarOrdem = ({ estaAberto, aoFechar, ordem }) => {
  if (!estaAberto || !ordem) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container modal-visualizar">
        <div className="modal-header">
          <h2>Detalhes da Ordem</h2>
          <button className="botao-fechar" onClick={aoFechar}>
            &times;
          </button>
        </div>

        <div className="conteudo-visualizacao">
          <div className="grupo-visualizacao">
            <h3>Informações gerais</h3>
            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Cliente</span>
                <div className="valor">{ordem.cliente || "—"}</div>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Funcionário</span>
                <div className="valor">{ordem.funcionario || "—"}</div>
              </div>
            </div>

            <h3>Serviços</h3>
            {(ordem.servicos || []).length === 0 && (
              <div className="campo-visualizacao">
                <div className="valor">Nenhum serviço</div>
              </div>
            )}
            {(ordem.servicos || []).map((s, i) => (
              <div className="linha-visualizacao" key={i}>
                <div className="campo-visualizacao">
                  <span className="rotulo">Serviço</span>
                  <div className="valor">{s.servico || "—"}</div>
                </div>
                <div className="campo-visualizacao">
                  <span className="rotulo">Desconto</span>
                  <div className="valor">{s.desconto ?? 0}%</div>
                </div>
              </div>
            ))}

            <h3>Produtos</h3>
            {(ordem.produtos || []).length === 0 && (
              <div className="campo-visualizacao">
                <div className="valor">Nenhum produto</div>
              </div>
            )}
            {(ordem.produtos || []).map((p, i) => (
              <div className="linha-visualizacao" key={i}>
                <div className="campo-visualizacao">
                  <span className="rotulo">Produto</span>
                  <div className="valor">{p.produto || "—"}</div>
                </div>
                <div className="campo-visualizacao">
                  <span className="rotulo">Quantidade</span>
                  <div className="valor">{p.quantidade ?? 1}</div>
                </div>
                <div className="campo-visualizacao">
                  <span className="rotulo">Desconto</span>
                  <div className="valor">{p.desconto ?? 0}%</div>
                </div>
              </div>
            ))}

            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Quantidade total</span>
                <div className="valor">{ordem.quantidade || "—"}</div>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Valor total (R$)</span>
                <div className="valor">{ordem.valorTotal || "0,00"}</div>
              </div>
            </div>

            <h3>Observações</h3>
            <div className="campo-visualizacao">
              <div className="valor">{ordem.observacoes || "—"}</div>
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

const OrdemServico = () => {
  const [ordens, setOrdens] = useState([]);
  const [modalAberto, setModalAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmAberto, setModalConfirmAberto] = useState(false);
  const [ordemEmEdicao, setOrdemEmEdicao] = useState(null);
  const [ordemParaVisualizar, setOrdemParaVisualizar] = useState(null);
  const [ordemParaExcluir, setOrdemParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  // listas vindas do catálogo (serão atualizadas via subscription)
  const [listaServicos, setListaServicos] = useState(
    CatalogService.getServicos()
  );
  const [listaProdutos, setListaProdutos] = useState(
    CatalogService.getProdutos()
  );

  useEffect(() => {
    const unsubS = CatalogService.subscribeServicos((next) =>
      setListaServicos(next)
    );
    const unsubP = CatalogService.subscribeProdutos((next) =>
      setListaProdutos(next)
    );
    return () => {
      unsubS();
      unsubP();
    };
  }, []);

  const abrirNovaOrdem = () => {
    setOrdemEmEdicao(null);
    setModalAberto(true);
  };

  const salvarOrdem = (dados) => {
    if (dados.id) {
      setOrdens(ordens.map((o) => (o.id === dados.id ? dados : o)));
      success("Ordem atualizada com sucesso");
    } else {
      setOrdens([{ id: Date.now(), ...dados }, ...ordens]);
      success("Ordem adicionada com sucesso");
    }
  };

  const visualizar = (ordem) => {
    setOrdemParaVisualizar(ordem);
    setModalVisualizarAberto(true);
  };

  const editar = (ordem) => {
    setOrdemEmEdicao(ordem);
    setModalAberto(true);
  };

  const prepararExclusao = (id) => {
    setOrdemParaExcluir(id);
    setModalConfirmAberto(true);
  };

  const confirmarExclusao = () => {
    setOrdens(ordens.filter((o) => o.id !== ordemParaExcluir));
    setModalConfirmAberto(false);
    setOrdemParaExcluir(null);
    success("Ordem excluída");
  };

  const ordensFiltradas = ordens.filter((o) => {
    if (!termoPesquisa) return true;
    const t = termoPesquisa.toLowerCase();
    return (
      (o.cliente && o.cliente.toLowerCase().includes(t)) ||
      (o.funcionario && o.funcionario.toLowerCase().includes(t))
    );
  });

  return (
    <div className="container-ordens">
      <h1>Ordens de Serviço</h1>

      <div className="container-pesquisa">
        <input
          type="text"
          placeholder="Pesquisar por cliente, serviço ou produto..."
          className="campo-pesquisa"
          value={termoPesquisa}
          onChange={(e) => setTermoPesquisa(e.target.value)}
        />
        <button className="botao-adicionar" onClick={abrirNovaOrdem}>
          Adicionar Ordem
        </button>
      </div>

      <div className="lista-ordens">
        {ordensFiltradas.map((o) => (
          <div className="ordem-card" key={o.id}>
            <div className="ordem-info">
              <h3>{o.cliente || "—"}</h3>
              <div className="linha-detalhes">
                <div>
                  <strong>Funcionário</strong>
                  <div className="sub">{o.funcionario || "—"}</div>
                </div>
                <div>
                  <strong>Serviço</strong>
                  <div className="sub">
                    {(o.servicos || [])
                      .map((s) => s.servico)
                      .filter(Boolean)
                      .join(", ")}
                  </div>
                </div>
                <div>
                  <strong>Produto</strong>
                  <div className="sub">
                    {(o.produtos || [])
                      .map((p) => p.produto)
                      .filter(Boolean)
                      .join(", ")}
                  </div>
                </div>
                <div className="valor">R$ {o.valorTotal || "0,00"}</div>
              </div>
            </div>
            <div className="ordem-actions">
              <button
                className="botao-visualizar"
                onClick={() => visualizar(o)}
              >
                Visualizar
              </button>
              <button className="botao-editar" onClick={() => editar(o)}>
                Editar
              </button>
              <button
                className="botao-excluir"
                onClick={() => prepararExclusao(o.id)}
              >
                Excluir
              </button>
            </div>
          </div>
        ))}

        {ordensFiltradas.length === 0 && (
          <div className="sem-resultados">
            <p>Nenhuma ordem encontrada.</p>
          </div>
        )}
      </div>

      <ModalOrdem
        estaAberto={modalAberto}
        aoFechar={() => setModalAberto(false)}
        ordemInicial={ordemEmEdicao}
        aoSalvar={salvarOrdem}
        listaServicos={listaServicos}
        listaProdutos={listaProdutos}
      />

      <ModalVisualizarOrdem
        estaAberto={modalVisualizarAberto}
        aoFechar={() => setModalVisualizarAberto(false)}
        ordem={ordemParaVisualizar}
      />

      <ModalConfirmacao
        estaAberto={modalConfirmAberto}
        aoFechar={() => setModalConfirmAberto(false)}
        aoConfirmar={confirmarExclusao}
        titulo="Confirmar exclusão"
        mensagem="Deseja realmente excluir esta ordem?"
        textoBotaoConfirmar="Excluir"
        textoBotaoCancelar="Cancelar"
        tipo="exclusao"
      />
    </div>
  );
};

export default OrdemServico;
