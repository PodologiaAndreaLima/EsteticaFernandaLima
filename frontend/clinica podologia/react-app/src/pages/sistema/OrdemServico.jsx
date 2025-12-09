import React, { useState, useEffect } from "react";
import ModalConfirmacao from "../../components/sistema/ModalConfirmacao";
import { success, error } from "../../services/toastService";
import servicoProdutoService from "../../services/servicoProdutoService";
import produtoService from "../../services/produtoService";
import combosService from "../../services/combosService";
import ordemService from "../../services/ordemService";
import clienteService from "../../services/clienteService";
import usuarioService from "../../services/usuarioService";
import "./OrdemServico.css";

const ModalOrdem = ({
  estaAberto,
  aoFechar,
  ordemInicial,
  aoSalvar,
  listaServicos,
  listaProdutos,
  listaCombos,
  listaClientes,
  listaUsuarios,
}) => {
  const [ordem, setOrdem] = useState({
    clienteId: "",
    usuarioId: "",
    servicos: [], // { servico: id, desconto }
    produtos: [], // { produto: id, quantidade, desconto }
    valorVenda: "",
    desconto: 0,
    observacoes: "",
    id: undefined,
  });

  useEffect(() => {
    if (!estaAberto) return;

    if (ordemInicial && (ordemInicial.idOrdemServico || ordemInicial.id)) {
      const servicos = (ordemInicial.itens || [])
        .filter((it) => {
          if (it.combo) return true;
          if (it.servicoProduto && !it.servicoProduto.isProduto && !it.servicoProduto.produto) return true;
          return false;
        })
        .map((it) => {
          if (it.combo) return { servico: it.combo.idCombo ?? it.combo.id, desconto: it.desconto ?? 0 };
          if (it.servicoProduto) return { servico: it.servicoProduto.idProdutoServico, desconto: it.desconto ?? 0 };
          return null;
        })
        .filter(Boolean);

      const produtos = (ordemInicial.itens || [])
        .filter((it) => it.servicoProduto?.isProduto === true || it.servicoProduto?.produto === true)
        .map((it) => ({
          produto: it.servicoProduto.idProdutoServico,
          quantidade: it.quantidade ?? 1,
          desconto: it.desconto ?? 0,
        }));

      setOrdem({
        clienteId: ordemInicial.cliente?.id ?? ordemInicial.clienteId ?? "",
        usuarioId: ordemInicial.usuario?.id ?? ordemInicial.usuarioId ?? "",
        servicos,
        produtos,
        valorVenda: ordemInicial.valorFinal ?? ordemInicial.valorVenda ?? "",
        desconto: ordemInicial.desconto ?? 0,
        observacoes: ordemInicial.observacao ?? ordemInicial.observacoes ?? "",
        id: ordemInicial.idOrdemServico ?? ordemInicial.id ?? undefined,
      });
    } else {
      setOrdem({
        clienteId: "",
        usuarioId: "",
        servicos: [],
        produtos: [],
        valorVenda: "",
        desconto: 0,
        observacoes: "",
        id: undefined,
      });
    }
  }, [estaAberto, ordemInicial]);

  const alterarCampo = (e) => {
    const { name, value } = e.target;
    setOrdem((o) => ({ ...o, [name]: value }));
  };

  const adicionarServico = () => {
    setOrdem((o) => ({ ...o, servicos: [...o.servicos, { servico: "", desconto: 0 }] }));
  };

  const removerServico = (idx) => {
    setOrdem((o) => ({ ...o, servicos: o.servicos.filter((_, i) => i !== idx) }));
  };

  const alterarServicoLinha = (idx, campo, valor) => {
    setOrdem((o) => ({
      ...o,
      servicos: o.servicos.map((s, i) => (i === idx ? { ...s, [campo]: valor } : s)),
    }));
  };

  const adicionarProduto = () => {
    setOrdem((o) => ({ ...o, produtos: [...o.produtos, { produto: "", quantidade: 1, desconto: 0 }] }));
  };

  const removerProduto = (idx) => {
    setOrdem((o) => ({ ...o, produtos: o.produtos.filter((_, i) => i !== idx) }));
  };

  const alterarProdutoLinha = (idx, campo, valor) => {
    setOrdem((o) => ({
      ...o,
      produtos: o.produtos.map((p, i) => (i === idx ? { ...p, [campo]: valor } : p)),
    }));
  };

  const enviar = async (e) => {
    e.preventDefault();

    try {
      console.log("DEBUG - ordem state:", ordem);
      console.log("DEBUG - ordem.servicos:", ordem.servicos);
      console.log("DEBUG - ordem.produtos:", ordem.produtos);
      console.log("DEBUG - listaServicos:", listaServicos);
      console.log("DEBUG - listaProdutos:", listaProdutos);
      
      const valorFinalNum = parseFloat(String(ordem.valorVenda).replace(",", ".")) || 0;
      const itens = [];

      console.log("DEBUG - ordem.servicos ANTES DO LOOP:", ordem.servicos, "length:", ordem.servicos?.length);

      // Processar serviços/combos
      for (const s of ordem.servicos) {
        const selectedId = Number(s.servico);
        console.log("DEBUG - processando servico:", s, "selectedId:", selectedId);
        if (!selectedId) continue;
        const isCombo = (listaCombos || []).some((c) => Number(c.id ?? c.idCombo) === selectedId);
        if (isCombo) {
          itens.push({ comboId: selectedId, quantidade: 1, desconto: parseFloat(s.desconto) || 0 });
        } else {
          itens.push({ servicoProdutoId: selectedId, quantidade: 1, desconto: parseFloat(s.desconto) || 0 });
        }
      }

      // Processar produtos (produtos são ServicoProduto com isProduto=true)
      for (const p of ordem.produtos) {
        const produtoId = Number(p.produto);
        console.log("DEBUG - processando produto:", p, "produtoId:", produtoId);
        if (!produtoId) continue;
        itens.push({
          produtoId: produtoId, // Backend trata como servicoProdutoId
          quantidade: Number(p.quantidade) || 1,
          desconto: parseFloat(p.desconto) || 0
        });
      }

      const payload = {
        clienteId: Number(ordem.clienteId) || null,
        usuarioId: Number(ordem.usuarioId) || null,
        valorFinal: parseFloat(String(valorFinalNum)) || 0,
        observacao: ordem.observacoes || "",
        itens,
      };

      console.log("DEBUG - PAYLOAD FINAL COMPLETO:", JSON.stringify(payload, null, 2));
      console.log("DEBUG - itens.length:", itens.length);

      await aoSalvar(payload, ordem.id);
    } catch (err) {
      console.error("Erro ao montar payload da ordem", err);
      error("Erro ao montar pedido");
    } finally {
      aoFechar();
    }
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
              <select name="clienteId" value={ordem.clienteId} onChange={alterarCampo} required>
                <option value="">-- selecione cliente --</option>
                {(listaClientes || []).map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nome || c.nomeCompleto || `Cliente ${c.id}`}
                  </option>
                ))}
              </select>
            </div>
            <div className="grupo-formulario">
              <label>Funcionário</label>
              <select name="usuarioId" value={ordem.usuarioId} onChange={alterarCampo} required>
                <option value="">-- selecione funcionário --</option>
                {(listaUsuarios || []).map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.nome || u.nomeCompleto || `Usuário ${u.id}`}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="linha-formulario">
            <button type="button" className="botao-adicionar botao-adicionar-grande" onClick={adicionarServico}>
              + Adicionar Serviço/Combo
            </button>
          </div>

          {ordem.servicos.map((s, idx) => (
            <div key={idx} className="linha-formulario pequena">
              <div className="grupo-formulario">
                <label>Serviço / Combo</label>
                <select value={s.servico} onChange={(e) => alterarServicoLinha(idx, "servico", e.target.value)}>
                  <option value="">-- selecione --</option>
                  {(listaServicos || []).map((sv) => (
                    <option key={sv.idProdutoServico} value={sv.idProdutoServico}>
                      {sv.nome ?? sv.descricao}
                    </option>
                  ))}
                  {(listaCombos || []).map((c) => (
                    <option key={c.id ?? c.idCombo} value={c.id ?? c.idCombo}>
                      {c.nome}
                    </option>
                  ))}
                </select>
              </div>
              <div className="grupo-formulario small">
                <label>Desconto (%)</label>
                <input type="number" min="0" value={s.desconto} onChange={(e) => alterarServicoLinha(idx, "desconto", e.target.value)} />
              </div>
              <div className="grupo-formulario tiny">
                <button type="button" className="botao-excluir-pequeno" onClick={() => removerServico(idx)} aria-label="Remover serviço">
                  ✖
                </button>
              </div>
            </div>
          ))}

          <div className="linha-formulario">
            <button type="button" className="botao-adicionar botao-adicionar-grande" onClick={adicionarProduto}>
              + Adicionar Produto
            </button>
          </div>

          {ordem.produtos.map((p, idx) => (
            <div key={idx} className="linha-formulario pequena">
              <div className="grupo-formulario">
                <label>Produto</label>
                <select value={p.produto} onChange={(e) => alterarProdutoLinha(idx, "produto", e.target.value)}>
                  <option value="">-- selecione --</option>
                  {(listaProdutos || []).map((pd) => (
                    <option key={pd.idProdutoServico} value={pd.idProdutoServico}>
                      {pd.nome}
                    </option>
                  ))}
                </select>
              </div>
              <div className="grupo-formulario small">
                <label>Quantidade</label>
                <input type="number" min="1" value={p.quantidade} onChange={(e) => alterarProdutoLinha(idx, "quantidade", e.target.value)} />
              </div>
              <div className="grupo-formulario small">
                <label>Desconto (%)</label>
                <input type="number" min="0" value={p.desconto} onChange={(e) => alterarProdutoLinha(idx, "desconto", e.target.value)} />
              </div>
              <div className="grupo-formulario tiny">
                <button type="button" className="botao-excluir-pequeno" onClick={() => removerProduto(idx)} aria-label="Remover produto">
                  ✖
                </button>
              </div>
            </div>
          ))}

          <div className="linha-formulario">
            <div className="grupo-formulario full">
              <label>Observações</label>
              <textarea value={ordem.observacoes} name="observacoes" onChange={alterarCampo} rows={4} />
            </div>
          </div>

          <div className="linha-formulario">
            <div className="grupo-formulario">
              <label>Valor de venda (R$)</label>
              <input name="valorVenda" value={ordem.valorVenda} onChange={alterarCampo} />
            </div>
            <div className="grupo-formulario">
              <label>Desconto (%)</label>
              <input type="number" min="0" name="desconto" value={ordem.desconto} onChange={alterarCampo} />
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

const ModalVisualizarOrdem = ({ estaAberto, aoFechar, ordem }) => {
  if (!estaAberto || !ordem) return null;

  console.log("DEBUG Modal - ordem completa:", ordem);
  console.log("DEBUG Modal - ordem.itens:", ordem.itens);

  // Separar serviços/combos de produtos
  const servs = (ordem.itens || [])
    .filter((it) => {
      console.log("DEBUG - Filtrando item para servicos:", it, "combo?", !!it.combo, "isProduto?", it.servicoProduto?.isProduto, "produto?", it.servicoProduto?.produto);
      if (it.combo) return true;
      if (it.servicoProduto && !it.servicoProduto.isProduto && !it.servicoProduto.produto) return true;
      return false;
    })
    .map((it) => (it.combo ? it.combo.nome : it.servicoProduto?.nome ?? it.servicoProduto?.descricao))
    .join(", ");
  
  console.log("DEBUG Modal - servs final:", servs);

  const prods = (ordem.itens || [])
    .filter((it) => it.servicoProduto?.isProduto === true || it.servicoProduto?.produto === true)
    .map((it) => `${it.servicoProduto.nome} x${it.quantidade ?? 1}`)
    .join(", ");

  console.log("DEBUG Modal - servs:", servs);
  console.log("DEBUG Modal - prods:", prods);

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
                <div className="valor">{ordem.cliente?.nome ?? ordem.cliente?.nomeCompleto ?? (typeof ordem.cliente === 'string' ? ordem.cliente : "—")}</div>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Funcionário</span>
                <div className="valor">{ordem.usuario?.nome ?? ordem.usuario?.nomeCompleto ?? "—"}</div>
              </div>
            </div>

            <div className="campo-visualizacao" style={{ marginTop: '20px' }}>
              <span className="rotulo">Serviços / Combos</span>
              <div className="valor">{servs || "Nenhum serviço adicionado"}</div>
            </div>

            <div className="campo-visualizacao" style={{ marginTop: '15px' }}>
              <span className="rotulo">Produtos</span>
              <div className="valor">{prods || "Nenhum produto adicionado"}</div>
            </div>

            <div className="campo-visualizacao" style={{ marginTop: '15px' }}>
              <span className="rotulo">Observações</span>
              <div className="valor">{ordem.observacao ?? ordem.observacoes ?? "—"}</div>
            </div>

            <div className="linha-visualizacao">
              <div className="campo-visualizacao">
                <span className="rotulo">Valor de venda (R$)</span>
                <div className="valor">{ordem.valorFinal ?? "0,00"}</div>
              </div>
              <div className="campo-visualizacao">
                <span className="rotulo">Desconto (%)</span>
                <div className="valor">{ordem.desconto ?? 0}%</div>
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

const OrdemServico = () => {
  const [ordens, setOrdens] = useState([]);
  const [modalAberto, setModalAberto] = useState(false);
  const [modalVisualizarAberto, setModalVisualizarAberto] = useState(false);
  const [modalConfirmAberto, setModalConfirmAberto] = useState(false);
  const [ordemEmEdicao, setOrdemEmEdicao] = useState(null);
  const [ordemParaVisualizar, setOrdemParaVisualizar] = useState(null);
  const [ordemParaExcluir, setOrdemParaExcluir] = useState(null);
  const [termoPesquisa, setTermoPesquisa] = useState("");

  const [listaServicos, setListaServicos] = useState([]);
  const [listaProdutos, setListaProdutos] = useState([]);
  const [listaCombos, setListaCombos] = useState([]);
  const [listaClientes, setListaClientes] = useState([]);
  const [listaUsuarios, setListaUsuarios] = useState([]);

  useEffect(() => {
    const load = async () => {
      try {
        const unwrap = (r) => {
          if (!r) return [];
          if (Array.isArray(r)) return r;
          if (r.data) return r.data;
          return r;
        };

        const [
          sRes,
          pRes,
          cRes,
          ordRes,
          clientesRes,
        ] = await Promise.allSettled([
          servicoProdutoService.list?.() ?? servicoProdutoService.listAll?.() ?? Promise.resolve([]),
          produtoService.list?.() ?? produtoService.listAll?.() ?? Promise.resolve([]),
          combosService.list?.() ?? combosService.listAll?.() ?? Promise.resolve([]),
          ordemService.list?.() ?? ordemService.listAll?.() ?? Promise.resolve([]),
          clienteService?.list?.() ?? Promise.resolve([]),
        ]);

        const servicos = sRes.status === "fulfilled" ? unwrap(sRes.value) : [];
        const produtos = pRes.status === "fulfilled" ? unwrap(pRes.value) : [];
        const combos = cRes.status === "fulfilled" ? unwrap(cRes.value) : [];
        const ordensRaw = ordRes.status === "fulfilled" ? unwrap(ordRes.value) : [];
        const clientes = clientesRes.status === "fulfilled" ? unwrap(clientesRes.value) : [];
        
        // Buscar todos os usuários do backend
        const usuariosRes = await Promise.allSettled([usuarioService.list()]);
        const usuariosBackend = usuariosRes[0].status === "fulfilled" ? unwrap(usuariosRes[0].value) : [];
        
        console.log("DEBUG - usuariosBackend:", usuariosBackend);
        
        // Usar todos os usuários do backend
        const usuarios = usuariosBackend;

        console.log("DEBUG - Clientes:", clientes);
        console.log("DEBUG - Usuário Logado:", usuarios);
        console.log("DEBUG - Servicos (sem produtos):", servicos);
        console.log("DEBUG - Produtos (só produtos):", produtos);
        console.log("DEBUG - Ordens:", ordensRaw);
        if (ordensRaw.length > 0) {
          console.log("DEBUG - Primeira ordem com itens:", ordensRaw[0]);
          console.log("DEBUG - Itens da primeira ordem:", ordensRaw[0].itens);
        }

        setListaServicos(servicos);
        setListaProdutos(produtos);
        setListaCombos(combos);
        setListaClientes(clientes);
        setListaUsuarios(usuarios);

        setOrdens(Array.isArray(ordensRaw) ? ordensRaw : []);
      } catch (err) {
        console.error("Erro ao carregar catálogos/ordens", err);
        error("Erro ao carregar dados");
      }
    };

    load();
  }, []);

  const abrirNovaOrdem = () => {
    setOrdemEmEdicao(null);
    setModalAberto(true);
  };

  const salvarOrdem = async (payload, id) => {
    try {
      console.log("DEBUG - Payload antes de salvar:", JSON.stringify(payload, null, 2));
      if (id) {
        await ordemService.update?.(id, payload) ?? ordemService.updateOrder?.(id, payload);
        success("Ordem atualizada com sucesso");
      } else {
        const result = await ordemService.create?.(payload) ?? ordemService.createOrder?.(payload);
        console.log("DEBUG - Resultado após criar:", result);
        success("Ordem adicionada com sucesso");
      }
      const r = await ordemService.list?.() ?? ordemService.listAll?.();
      const ordensArr = r?.data ?? r ?? [];
      console.log("DEBUG - Ordens após salvar:", ordensArr);
      if (ordensArr.length > 0) {
        console.log("DEBUG - Última ordem salva:", ordensArr[ordensArr.length - 1]);
      }
      setOrdens(Array.isArray(ordensArr) ? ordensArr : []);
    } catch (err) {
      console.error("Erro ao salvar ordem", err);
      error("Erro ao salvar ordem: " + (err?.response?.data?.message || err.message || ""));
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

  const confirmarExclusao = async () => {
    try {
      await ordemService.remove?.(ordemParaExcluir) ?? ordemService.delete?.(ordemParaExcluir);
      success("Ordem excluída");
      const r = await ordemService.list?.() ?? ordemService.listAll?.();
      const ordensArr = r?.data ?? r ?? [];
      setOrdens(Array.isArray(ordensArr) ? ordensArr : []);
    } catch (err) {
      console.error("Erro ao excluir ordem", err);
      error("Erro ao excluir ordem");
    } finally {
      setModalConfirmAberto(false);
      setOrdemParaExcluir(null);
    }
  };

  const ordensFiltradas = (Array.isArray(ordens) ? ordens : []).filter((o) => {
    if (!termoPesquisa) return true;
    const t = termoPesquisa.toLowerCase();
    const clienteName = o.cliente?.nome ?? o.cliente?.nomeCompleto ?? (typeof o.cliente === 'string' ? o.cliente : "");
    const usuarioName = o.usuario?.nome ?? o.usuario?.nomeCompleto ?? (typeof o.usuario === 'string' ? o.usuario : "");
    return (
      (clienteName && String(clienteName).toLowerCase().includes(t)) ||
      (usuarioName && String(usuarioName).toLowerCase().includes(t)) ||
      (o.idOrdemServico && String(o.idOrdemServico).includes(t))
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
          <div className="ordem-card" key={o.idOrdemServico ?? o.id ?? Date.now()}>
            <div className="ordem-info">
              <h3>{o.cliente?.nome ?? o.cliente?.nomeCompleto ?? (typeof o.cliente === 'string' ? o.cliente : "—")}</h3>
              <div className="linha-detalhes">
                <div>
                  <strong>Funcionário</strong>
                  <div className="sub">{o.usuario?.nome ?? o.usuario?.nomeCompleto ?? "—"}</div>
                </div>
                <div>
                  <strong>Serviço</strong>
                  <div className="sub">
                    {(o.itens || [])
                      .filter((it) => {
                        if (it.combo) return true;
                        if (it.servicoProduto && !it.servicoProduto.isProduto && !it.servicoProduto.produto) return true;
                        return false;
                      })
                      .map((it) => (it.combo ? it.combo.nome : it.servicoProduto?.nome ?? it.servicoProduto?.descricao))
                      .filter(Boolean)
                      .join(", ")}
                  </div>
                </div>
                <div>
                  <strong>Produto</strong>
                  <div className="sub">
                    {(o.itens || [])
                      .filter((it) => it.servicoProduto?.isProduto === true || it.servicoProduto?.produto === true)
                      .map((it) => `${it.servicoProduto.nome} x${it.quantidade ?? 1}`)
                      .join(", ")}
                  </div>
                </div>
                <div className="valor">R$ {o.valorFinal ?? o.valorVenda ?? "0,00"}</div>
              </div>
            </div>
            <div className="ordem-actions">
              <button className="botao-visualizar" onClick={() => visualizar(o)}>
                Visualizar
              </button>
              <button className="botao-editar" onClick={() => editar(o)}>
                Editar
              </button>
              <button className="botao-excluir" onClick={() => prepararExclusao(o.idOrdemServico ?? o.id)}>
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
        listaCombos={listaCombos}
        listaClientes={listaClientes}
        listaUsuarios={listaUsuarios}
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