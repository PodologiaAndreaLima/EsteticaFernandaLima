import React, { useEffect, useRef, useState } from "react";
import "../../styles/Dashboard.css";
import Chart from "chart.js/auto";
import { dashboardService } from "../../services/dashboardService";

const Dashboard = () => {
  const lineRef = useRef(null);
  const pieRef = useRef(null);
  const servicesRef = useRef(null);
  const productsRef = useRef(null);
  const detailServicesRef = useRef(null);
  const detailProductsRef = useRef(null);
  const detailBar1Ref = useRef(null);
  const detailDoughRef = useRef(null);
  const finalTopLeftRef = useRef(null);
  const finalTopRightRef = useRef(null);
  const finalBottomRef = useRef(null);
  const [view, setView] = useState("simples");
  const [rendaBruta, setRendaBruta] = useState(0.00);
  const [rendaLiquida, setRendaLiquida] = useState(0.00);
  const [rendaBrutaMeses, setRendaBrutaMeses] = useState({});
  const [rendaLiquidaMeses, setRendaLiquidaMeses] = useState({});
  const [totalOrdemServico, setTotalOrdemServico] = useState(0);
  const [servicosCombosMaisVendidosMes, setServicosCombosMaisVendidosMes] = useState([]);
  const [produtosMaisVendidosMes, setProdutosMaisVendidosMes] = useState([]);
  const [receitaFuncionarioMesAtual, setReceitaFuncionarioMesAtual] = useState(0.00);
  const [proprietorCommission, setProprietorCommission] = useState(0.00);
  const [quantidadeOrdensServicoFuncionario, setQuantidadeOrdensServicoFuncionario] = useState(0);
  const commissionRate = 0.3; // 30% commission rate

  // Estados para dashboard detalhada
  const [detalhesMesAtual, setDetalhesMesAtual] = useState({
    receitaBruta: 0.00,
    receitaLiquida: 0.00,
    custosFixos: 0.00,
    custosExtras: 0.00,
    margemLucro: 0.00,
    variacaoReceita: 0.00,
    variacaoCustos: 0.00,
    numeroOrdens: 0,
    ticketMedio: 0.00,
    custoMedioPorServico: 0.00,
    topServicos: [],
    topProdutos: [],
    atendimentosPorDia: {},
    proporcaoAtendimentos: {},
    receitaPorFuncionario: {},
    comissaoProprietario: 0.00,
    ordensPorFuncionario: {},
    atendimentosFuncionarioPorDia: {}
  });

  // Primeiro useEffect: buscar dados
  useEffect(() => {
    const fetchReceitaData = async () => {
      try {
        const resultadoBruta = await dashboardService.getRendaBrutaMesAtual();
        if (resultadoBruta.sucess) {
          setRendaBruta(resultadoBruta.data);
        }

        const resultadoLiquida = await dashboardService.getRendaLiquidaMesAtual();
        if (resultadoLiquida.sucess) {
          setRendaLiquida(resultadoLiquida.data);
        }

        const resultadoBrutaMeses = await dashboardService.getRendaBrutaTodosMeses();
        if (resultadoBrutaMeses.sucess) {
          setRendaBrutaMeses(resultadoBrutaMeses.data);
        }

        const resultadoLiquidaMeses = await dashboardService.getRendaLiquidaTodosMeses();
        if (resultadoLiquidaMeses.sucess) {
          setRendaLiquidaMeses(resultadoLiquidaMeses.data);
        }

        const totalOrdemServico = await dashboardService.getTotalOrdensServicos();
        if (totalOrdemServico.sucess) {
          setTotalOrdemServico(totalOrdemServico.data);
        }

        const servicosCombosMaisVendidosMes = await dashboardService.getServicosCombosMaisVendidosMes();
        if (servicosCombosMaisVendidosMes.sucess) {
          setServicosCombosMaisVendidosMes(servicosCombosMaisVendidosMes.data);
        }

        const produtosMaisVendidosMes =  await dashboardService.getProdutosMaisVendidosMes();
        if (produtosMaisVendidosMes.sucess) {
          setProdutosMaisVendidosMes(produtosMaisVendidosMes.data);
        }

        const receitaFuncionario = await dashboardService.getReceitaFuncionarioMesAtual();
        if (receitaFuncionario.sucess) {
          setReceitaFuncionarioMesAtual(receitaFuncionario.data);
          setProprietorCommission(receitaFuncionario.data * commissionRate);
        }

        const quantidadeOrdensServicoFuncionario = await dashboardService.getTotalOrdensServicosFuncionarioMesAtual();
        if (quantidadeOrdensServicoFuncionario.sucess) {
          setQuantidadeOrdensServicoFuncionario(quantidadeOrdensServicoFuncionario.data);
        }
      } catch (error) {
        console.error("Erro ao buscar dados de receita:", error);
      }
    };
    fetchReceitaData();
  }, []);

  // useEffect para buscar dados detalhados quando a view é "detalhada"
  useEffect(() => {
    if (view === "detalhada") {
      const fetchDetalhesData = async () => {
        const hoje = new Date();
        const mes = hoje.getMonth() + 1; // getMonth() retorna 0-11
        const ano = hoje.getFullYear();

        try {
          const [
            receitaBrutaRes,
            receitaLiquidaRes,
            custosFixosRes,
            custosExtrasRes,
            margemLucroRes,
            variacaoRes,
            numeroOrdensRes,
            ticketMedioRes,
            custoMedioRes,
            topServicosRes,
            topProdutosRes,
            atendimentosDiaRes,
            proporcaoRes,
            receitaFuncRes,
            ordensFuncRes
          ] = await Promise.all([
            dashboardService.getReceitaBrutaMensal(mes, ano),
            dashboardService.getReceitaLiquidaMensal(mes, ano),
            dashboardService.getCustosFixosMensais(mes, ano),
            dashboardService.getCustosExtrasMensais(mes, ano),
            dashboardService.getMargemLucroBruto(mes, ano),
            dashboardService.getVariacaoMensal(mes, ano),
            dashboardService.getNumeroOrdensMensal(mes, ano),
            dashboardService.getTicketMedio(mes, ano),
            dashboardService.getCustoMedioPorServico(mes, ano),
            dashboardService.getTopServicos(mes, ano, 5),
            dashboardService.getTopProdutos(mes, ano, 5),
            dashboardService.getAtendimentosPorDiaSemana(mes, ano),
            dashboardService.getProporcaoAtendimentos(mes, ano),
            dashboardService.getReceitaPorFuncionario(mes, ano),
            dashboardService.getOrdensPorFuncionario(mes, ano)
          ]);

          setDetalhesMesAtual({
            receitaBruta: receitaBrutaRes.sucess ? receitaBrutaRes.data : 0.00,
            receitaLiquida: receitaLiquidaRes.sucess ? receitaLiquidaRes.data : 0.00,
            custosFixos: custosFixosRes.sucess ? custosFixosRes.data : 0.00,
            custosExtras: custosExtrasRes.sucess ? custosExtrasRes.data : 0.00,
            margemLucro: margemLucroRes.sucess ? margemLucroRes.data : 0.00,
            variacaoReceita: variacaoRes.sucess ? variacaoRes.data.variacaoReceita : 0.00,
            variacaoCustos: variacaoRes.sucess ? variacaoRes.data.variacaoCustos : 0.00,
            numeroOrdens: numeroOrdensRes.sucess ? numeroOrdensRes.data : 0,
            ticketMedio: ticketMedioRes.sucess ? ticketMedioRes.data : 0.00,
            custoMedioPorServico: custoMedioRes.sucess ? custoMedioRes.data : 0.00,
            topServicos: topServicosRes.sucess ? topServicosRes.data : [],
            topProdutos: topProdutosRes.sucess ? topProdutosRes.data : [],
            atendimentosPorDia: atendimentosDiaRes.sucess ? atendimentosDiaRes.data : {},
            proporcaoAtendimentos: proporcaoRes.sucess ? proporcaoRes.data : {},
            receitaPorFuncionario: receitaFuncRes.sucess ? receitaFuncRes.data : {},
            ordensPorFuncionario: ordensFuncRes.sucess ? ordensFuncRes.data : {},
            comissaoProprietario: receitaLiquidaRes.sucess ? receitaLiquidaRes.data * commissionRate : 0.00,
            atendimentosFuncionarioPorDia: atendimentosDiaRes.sucess ? atendimentosDiaRes.data : {}
          });
        } catch (error) {
          console.error("Erro ao buscar dados detalhados:", error);
        }
      };
      fetchDetalhesData();
    }
  }, [view]);

  useEffect(() => {
    console.log("useEffect acionado");
    console.log("rendaBrutaMeses:", rendaBrutaMeses);
    console.log("rendaLiquidaMeses:", rendaLiquidaMeses);
    console.log("servicosCombosMaisVendidosMes:", servicosCombosMaisVendidosMes);
    console.log("produtosMaisVendidosMes:", produtosMaisVendidosMes);
    console.log("receitaFuncionarioMesAtual:", receitaFuncionarioMesAtual);
    const charts = [];

    // Validação melhorada: verifica se tem pelo menos alguns dados
    const temDadosMeses = Object.keys(rendaBrutaMeses).length > 0 && Object.keys(rendaLiquidaMeses).length > 0;
    const temDadosProdutos = Array.isArray(servicosCombosMaisVendidosMes) && servicosCombosMaisVendidosMes.length > 0;
    const temDadosProdutosMaisVendidos = Array.isArray(produtosMaisVendidosMes) && produtosMaisVendidosMes.length > 0;

    console.log("temDadosMeses:", temDadosMeses);
    console.log("temDadosProdutos:", temDadosProdutos);
    console.log("temDadosProdutosMaisVendidos:", temDadosProdutosMaisVendidos);

    if (!temDadosMeses) {
      console.log("Aguardando dados de meses...");
      return;
    }
    
    //==========================================
    //      DASHBOARD SIMPLES CHARTS
    //==========================================
    
    if (lineRef.current) {
      try {
        const meses = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        const dataBruta = meses.map((_, i) => rendaBrutaMeses[i + 1] || 0);
        const dataLiquida = meses.map((_, i) => rendaLiquidaMeses[i + 1] || 0);

        charts.push(
          new Chart(lineRef.current, {
            type: "line",
            data: {
              labels: [
                "Jan",
                "Feb",
                "Mar",
                "Apr",
                "May",
                "Jun",
                "Jul",
                "Aug",
                "Sep",
                "Oct",
                "Nov",
                "Dec",
              ],
              datasets: [
                {
                  label: "Receita Bruta",
                  data: dataBruta,
                  borderColor: "#6c63ff",
                  backgroundColor: "rgba(108,99,255,0.08)",
                  tension: 0.3,
                  fill: true,
                },
                {
                  label: "Receita Líquida",
                  data: dataLiquida,
                  borderColor: "#ff7ab6",
                  backgroundColor: "rgba(255,122,182,0.06)",
                  tension: 0.3,
                  fill: true,
                },
              ],
            },
            options: {
              plugins: { legend: { display: false } },
              maintainAspectRatio: false,
            },
          })
        );
      } catch (err) {
        console.error("Error creating line chart:", err);
      }
    }

    if (pieRef.current) {
      try {
        charts.push(
          new Chart(pieRef.current, {
            type: "doughnut",
            data: {
              labels: ["Fixos", "Extras", "Despesas"],
              datasets: [
                {
                  data: [40, 35, 25],
                  backgroundColor: ["#6c63ff", "#ff7ab6", "#76e4f7"],
                },
              ],
            },
            options: {
              plugins: {
                legend: {
                  position: "right",
                  labels: { usePointStyle: true, boxWidth: 12, padding: 16 },
                },
              },
              maintainAspectRatio: false,
            },
          })
        );
      } catch (err) {
        console.error("Error creating doughnut chart:", err);
      }
    }

    if (servicesRef.current) {
      try {
        const produtoServicoLabels = servicosCombosMaisVendidosMes.map(item => item.nome);
        const produtoServicoData = servicosCombosMaisVendidosMes.map(item => item.quantidade);
        charts.push(
          new Chart(servicesRef.current, {
            type: "bar",
            data: {
              labels: produtoServicoLabels,
              datasets: [
                {
                  label: "Serviços/combos",
                  data: produtoServicoData,
                  backgroundColor: "rgba(108,99,255,0.95)",
                  barThickness: 10,
                  maxBarThickness: 14,
                  borderRadius: 6,
                  borderSkipped: false,
                  categoryPercentage: 0.6,
                  barPercentage: 0.5,
                },
              ],
            },
            options: {
              plugins: { legend: { display: false } },
              scales: {
                x: {
                  grid: { display: false },
                  ticks: { color: "#6d4b86" },
                },
                y: {
                  beginAtZero: true,
                  ticks: { stepSize: 20, color: "#9b8aa8" },
                  grid: { color: "rgba(0,0,0,0.06)" },
                },
              },
              maintainAspectRatio: false,
              responsive: true,
              interaction: { intersect: false, mode: "index" },
            },
          })
        );
      } catch (err) {
        console.error("Error creating services bar chart:", err);
      }
    }

    if (productsRef.current) {
      try {
        charts.push(
          new Chart(productsRef.current, {
            type: "bar",
            data: {
              labels: produtosMaisVendidosMes.map(item => item.nome),
              datasets: [
                {
                  label: "Produtos",
                  data: produtosMaisVendidosMes.map(item => item.quantidade),
                  backgroundColor: "rgba(179,136,255,0.95)",
                  barThickness: 10,
                  maxBarThickness: 14,
                  borderRadius: 6,
                  borderSkipped: false,
                  categoryPercentage: 0.6,
                  barPercentage: 0.5,
                },
              ],
            },
            options: {
              plugins: { legend: { display: false } },
              scales: {
                x: { grid: { display: false }, ticks: { color: "#6d4b86" } },
                y: {
                  beginAtZero: true,
                  ticks: { stepSize: 20, color: "#9b8aa8" },
                  grid: { color: "rgba(0,0,0,0.06)" },
                },
              },
              maintainAspectRatio: false,
              responsive: true,
              interaction: { intersect: false, mode: "index" },
            },
          })
        );
      } catch (err) {
        console.error("Error creating products bar chart:", err);
      }
    }

    //==========================================
    //      DASHBOARD DETALHADO CHARTS
    //==========================================
    if (view === "detalhada") {
      // additional charts used on the detailed extra card: services & products (top row)
      if (detailServicesRef.current) {
        try {
          const topServicosLabels = detalhesMesAtual.topServicos.map(item => item.nome);
          const topServicosData = detalhesMesAtual.topServicos.map(item => item.quantidade);
          charts.push(
            new Chart(detailServicesRef.current, {
              type: "bar",
              data: {
                labels: topServicosLabels,
                datasets: [
                  {
                    label: "Serviços/combos",
                    data: topServicosData,
                    backgroundColor: "rgba(108,99,255,0.95)",
                    borderRadius: 6,
                    barThickness: 10,
                    maxBarThickness: 14,
                    borderSkipped: false,
                    categoryPercentage: 0.6,
                    barPercentage: 0.5,
                  },
                ],
              },
              options: {
                plugins: { legend: { display: false } },
                scales: {
                  x: { grid: { display: false } },
                  y: { beginAtZero: true, grid: { color: "rgba(0,0,0,0.06)" } },
                },
                maintainAspectRatio: false,
              },
            })
          );
        } catch (err) {
          console.error("Error creating detail services chart:", err);
        }
      }

      if (detailProductsRef.current) {
        try {
          const topProdutosLabels = detalhesMesAtual.topProdutos.map(item => item.nome);
          const topProdutosData = detalhesMesAtual.topProdutos.map(item => item.quantidade);
          charts.push(
            new Chart(detailProductsRef.current, {
              type: "bar",
              data: {
                labels: topProdutosLabels,
                datasets: [
                  {
                    label: "Produtos",
                    data: topProdutosData,
                    backgroundColor: "rgba(179,136,255,0.95)",
                    borderRadius: 6,
                    barThickness: 10,
                    maxBarThickness: 14,
                    borderSkipped: false,
                    categoryPercentage: 0.6,
                    barPercentage: 0.5,
                  },
                ],
              },
              options: {
                plugins: { legend: { display: false } },
                scales: {
                  x: { grid: { display: false } },
                  y: { beginAtZero: true, grid: { color: "rgba(0,0,0,0.06)" } },
                },
                maintainAspectRatio: false,
              },
            })
          );
        } catch (err) {
          console.error("Error creating detail products chart:", err);
        }
      }

      // Charts for detailed extra card
      if (detailBar1Ref.current) {
        try {
          const diasSemana = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"];
          const atendimentosDiaData = diasSemana.map(dia => detalhesMesAtual.atendimentosPorDia[dia] || 0);
          charts.push(
            new Chart(detailBar1Ref.current, {
              type: "bar",
              data: {
                labels: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"],
                datasets: [
                  {
                    label: "Qtde atendimentos",
                    data: atendimentosDiaData,
                    backgroundColor: "rgba(108,99,255,0.9)",
                    borderRadius: 6,
                    barThickness: 12,
                    maxBarThickness: 16,
                    borderSkipped: false,
                    categoryPercentage: 0.6,
                    barPercentage: 0.5,
                  },
                ],
              },
              options: {
                plugins: { legend: { display: false } },
                scales: {
                  x: { grid: { display: false } },
                  y: { beginAtZero: true, grid: { color: "rgba(0,0,0,0.06)" } },
                },
                maintainAspectRatio: false,
              },
            })
          );
        } catch (err) {
          console.error("Error creating detail bar1 chart:", err);
        }
      }

      if (detailDoughRef.current) {
        try {
          const proporcaoData = [
            detalhesMesAtual.proporcaoAtendimentos.servicos || 0,
            detalhesMesAtual.proporcaoAtendimentos.combos || 0,
            detalhesMesAtual.proporcaoAtendimentos.produtos || 0
          ];
          charts.push(
            new Chart(detailDoughRef.current, {
              type: "doughnut",
              data: {
                labels: ["Serviços", "Combos", "Produtos"],
                datasets: [
                  {
                    data: proporcaoData,
                    backgroundColor: ["#6c63ff", "#ff7ab6", "#76e4f7"],
                  },
                ],
              },
              options: {
                plugins: { legend: { display: false } },
                maintainAspectRatio: false,
              },
            })
          );
        } catch (err) {
          console.error("Error creating detail doughnut chart:", err);
        }
      }

      // final detailed card charts (3 charts)
      if (finalTopLeftRef.current) {
        try {
          const topServicosLabels = detalhesMesAtual.topServicos.map(item => item.nome);
          const topServicosData = detalhesMesAtual.topServicos.map(item => item.quantidade);
          charts.push(
            new Chart(finalTopLeftRef.current, {
              type: "bar",
              data: {
                labels: topServicosLabels,
                datasets: [
                  {
                    data: topServicosData,
                    backgroundColor: "rgba(108,99,255,0.9)",
                    borderRadius: 6,
                    barThickness: 10,
                    maxBarThickness: 14,
                    borderSkipped: false,
                    categoryPercentage: 0.6,
                    barPercentage: 0.5,
                  },
                ],
              },
              options: {
                plugins: { legend: { display: false } },
                maintainAspectRatio: false,
              },
            })
          );
        } catch (err) {
          console.error("Error creating final top-left chart:", err);
        }
      }

      if (finalTopRightRef.current) {
        try {
          const topProdutosLabels = detalhesMesAtual.topProdutos.map(item => item.nome);
          const topProdutosData = detalhesMesAtual.topProdutos.map(item => item.quantidade);
          charts.push(
            new Chart(finalTopRightRef.current, {
              type: "bar",
              data: {
                labels: topProdutosLabels,
                datasets: [
                  {
                    data: topProdutosData,
                    backgroundColor: "rgba(179,136,255,0.9)",
                    borderRadius: 6,
                    barThickness: 10,
                    maxBarThickness: 14,
                    borderSkipped: false,
                    categoryPercentage: 0.6,
                    barPercentage: 0.5,
                  },
                ],
              },
              options: {
                plugins: { legend: { display: false } },
                maintainAspectRatio: false,
              },
            })
          );
        } catch (err) {
          console.error("Error creating final top-right chart:", err);
        }
      }

      if (finalBottomRef.current) {
        try {
          const diasSemana = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"];
          const atendimentosDiaData = diasSemana.map(dia => detalhesMesAtual.atendimentosPorDia[dia] || 0);
          charts.push(
            new Chart(finalBottomRef.current, {
              type: "bar",
              data: {
                labels: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"],
                datasets: [
                  {
                    data: atendimentosDiaData,
                    backgroundColor: "rgba(108,99,255,0.85)",
                    borderRadius: 6,
                    barThickness: 12,
                    maxBarThickness: 16,
                    borderSkipped: false,
                    categoryPercentage: 0.6,
                    barPercentage: 0.5,
                  },
                ],
              },
              options: {
                plugins: { legend: { display: false } },
                maintainAspectRatio: false,
              },
            })
          );
        } catch (err) {
          console.error("Error creating final bottom chart:", err);
        }
      }
    }

    // force a resize/update after render so charts appear correctly
    const resizeTimer = setTimeout(() => {
      charts.forEach((c) => {
        try {
          c.resize();
          c.update();
        } catch (e) {
          // ignore
        }
      });
    }, 120);

    return () => {
      clearTimeout(resizeTimer);
      charts.forEach((c) => c.destroy());
    };
  }, [view, rendaBrutaMeses, rendaLiquidaMeses, servicosCombosMaisVendidosMes, produtosMaisVendidosMes,
    receitaFuncionarioMesAtual, proprietorCommission, quantidadeOrdensServicoFuncionario, detalhesMesAtual
  ]);

  return (
    <div className="dashboard-container">
      <div className="dashboard-content">
        <div className="dashboard-header">
          <div className="title-group">
            <h1>Dashboard</h1>
            <div className="sub-title">Visão geral do negócio</div>
          </div>
          <div className="header-controls">
            <div className="view-toggle pill">
              <button
                className={`btn-secondary ${view === "simples" ? "active" : ""
                  }`}
                onClick={() => setView("simples")}
              >
                Simples
              </button>
              <button
                className={`btn-primary ${view === "detalhada" ? "active" : ""
                  }`}
                onClick={() => setView("detalhada")}
              >
                Detalhada
              </button>
            </div>
            <div className="period">
              Período atual
              <br />
              <strong>Março/26</strong>
            </div>
          </div>
        </div>

        {/* Render da view detalhada temporariamente desativado */}
        {view === "detalhada" && (
          <div className="dashboard-grid top-grid">
            <section className="dashboard-section metrics-card">
              <div className="metrics-row vertical">
                <div className="metrics-top">
                  <div className="metrics-left-col">
                    <div className="metrics-grid-2x3">
                      <div className="metric-item">
                        <span>Receita Bruta mensal</span>
                        <strong>R$ {detalhesMesAtual.receitaBruta.toFixed(2)}</strong>
                      </div>

                      <div className="metric-item">
                        <span>Receita Líquida mensal</span>
                        <strong>R$ {detalhesMesAtual.receitaLiquida.toFixed(2)}</strong>
                      </div>

                      <div className="metric-item">
                        <span>Custo Fixo mensal</span>
                        <strong>R$ {detalhesMesAtual.custosFixos.toFixed(2)}</strong>
                      </div>

                      <div className="metric-item">
                        <span>Custo Extra total do mês</span>
                        <strong>R$ {detalhesMesAtual.custosExtras.toFixed(2)}</strong>
                      </div>

                      <div className="metric-item variation-box">
                        <div className="variation-caption">
                          Variação em relação ao
                          <br />
                          mês anterior
                        </div>
                        <div className="variation-values">
                          <div className="variation-receita">
                            {detalhesMesAtual.variacaoReceita >= 0 ? '+' : ''}{detalhesMesAtual.variacaoReceita.toFixed(1)}% <span>Receita</span>
                          </div>
                          <div className="variation-custo">
                            {detalhesMesAtual.variacaoCustos >= 0 ? '+' : ''}{detalhesMesAtual.variacaoCustos.toFixed(1)}% <span>Custos</span>
                          </div>
                        </div>
                      </div>

                      <div className="metric-item gross-margin-box">
                        <div className="gross-margin">
                          Margem de Lucro Bruto
                          <br />
                          <strong>{detalhesMesAtual.margemLucro.toFixed(1)}%</strong>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="metrics-bottom">
                  <div className="metrics-charts">
                    <div className="line-chart">
                      <canvas ref={lineRef} />
                    </div>
                    <div className="doughnut-chart">
                      <canvas ref={pieRef} />
                    </div>
                  </div>
                </div>
              </div>
            </section>

            {/* Novo card detalhado: KPIs laterais + gráficos */}
            <section className="dashboard-section detailed-extra-card">
              <div className="detail-extra-inner">
                <div className="detail-kpis-left">
                  <div className="kpi-item">
                    <div className="kpi-label">
                      Número de ordens de serviço realizadas no mês
                    </div>
                    <div className="kpi-value">{detalhesMesAtual.numeroOrdens}</div>
                  </div>

                  <div className="kpi-item">
                    <div className="kpi-label">
                      Média de valor gasto por clientes (Ticket Médio)
                    </div>
                    <div className="kpi-value">R${detalhesMesAtual.ticketMedio.toFixed(2)}</div>
                  </div>

                  <div className="kpi-item">
                    <div className="kpi-label">Custo médio por serviço</div>
                    <div className="kpi-value">R${detalhesMesAtual.custoMedioPorServico.toFixed(2)}</div>
                  </div>
                </div>

                <div className="detail-charts-wrapper">
                  <div className="detail-charts-grid detail-grid-2x2">
                    <div className="detail-chart-box">
                      <div className="detail-chart-title">
                        5 Serviços/combos mais realizados no mês
                      </div>
                      <div className="detail-chart-canvas">
                        <canvas ref={detailServicesRef} />
                      </div>
                      <div className="detail-chart-legend">
                        <span
                          className="legend-color"
                          style={{ background: "#6c63ff" }}
                        ></span>{" "}
                        Serviços/combos
                      </div>
                    </div>

                    <div className="detail-chart-box">
                      <div className="detail-chart-title">
                        5 Produtos mais vendidos no mês
                      </div>
                      <div className="detail-chart-canvas">
                        <canvas ref={detailProductsRef} />
                      </div>
                      <div className="detail-chart-legend">
                        <span
                          className="legend-color"
                          style={{ background: "#b388ff" }}
                        ></span>{" "}
                        Produtos
                      </div>
                    </div>

                    <div className="detail-chart-box">
                      <div className="detail-chart-title">
                        Quantidade de atendimentos por dia da semana
                      </div>
                      <div className="detail-chart-canvas">
                        <canvas ref={detailBar1Ref} />
                      </div>
                      <div className="detail-chart-legend">
                        <span
                          className="legend-color"
                          style={{ background: "#6c63ff" }}
                        ></span>{" "}
                        Quantidade de atendimentos
                      </div>
                    </div>

                    <div className="detail-chart-box doughnut-side">
                      <div className="detail-chart-title">
                        Proporção de atendimentos realizados no mês
                      </div>
                      <div className="detail-dough-canvas">
                        <canvas ref={detailDoughRef} />
                      </div>
                      <div className="detail-chart-legend doughnut-legend">
                        <div className="legend-row">
                          <span
                            className="legend-color"
                            style={{ background: "#6c63ff" }}
                          ></span>{" "}
                          Serviços
                        </div>
                        <div className="legend-row">
                          <span
                            className="legend-color"
                            style={{ background: "#ff7ab6" }}
                          ></span>{" "}
                          Combos
                        </div>
                        <div className="legend-row">
                          <span
                            className="legend-color"
                            style={{ background: "#76e4f7" }}
                          ></span>{" "}
                          Produtos
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </section>

            {/* Último card detalhado: KPIs à esquerda + 3 gráficos à direita (2 cima, 1 embaixo) */}
            <section className="dashboard-section final-detailed-card">
              <div className="final-inner">
                <div className="final-kpis-left">
                  <div className="kpi-item">
                    <div className="kpi-label">
                      Receita mensal total gerada por funcionário
                    </div>
                    <div className="kpi-value">R${Object.values(detalhesMesAtual.receitaPorFuncionario).reduce((a, b) => a + b, 0).toFixed(2)}</div>
                  </div>

                  <div className="kpi-item">
                    <div className="kpi-label">Comissão da proprietária</div>
                    <div className="kpi-value">R${detalhesMesAtual.comissaoProprietario.toFixed(2)}</div>
                  </div>

                  <div className="kpi-item">
                    <div className="kpi-label">
                      Número de ordens de serviço realizadas no mês pelos
                      funcionários
                    </div>
                    <div className="kpi-value">{Object.values(detalhesMesAtual.ordensPorFuncionario).reduce((a, b) => a + b, 0)}</div>
                  </div>
                </div>

                <div className="final-charts-wrapper">
                  <div className="final-charts-grid">
                    <div className="final-chart-box">
                      <div className="final-chart-title">
                        5 Serviços/combos mais realizados no mês por
                        funcionários
                      </div>
                      <div className="final-chart-canvas">
                        <canvas ref={finalTopLeftRef} />
                      </div>
                    </div>

                    <div className="final-chart-box">
                      <div className="final-chart-title">
                        5 Produtos mais realizados no mês por funcionários
                      </div>
                      <div className="final-chart-canvas">
                        <canvas ref={finalTopRightRef} />
                      </div>
                    </div>

                    <div className="final-chart-box final-chart-bottom">
                      <div className="final-chart-title">
                        Quantidade de atendimentos de funcionários por dia da
                        semana
                      </div>
                      <div className="final-chart-canvas">
                        <canvas ref={finalBottomRef} />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </section>
            {/* Removed duplicate detailed-extra-card to keep only three cards in Detalhada view */}
          </div>
        )}

        {/* Render da view simples */}
        {view === "simples" && (
          <div className="dashboard-grid simple-grid">
            <section className="dashboard-section simple-card metrics-card">
              <div className="simple-inner-grid">
                <div className="simple-kpis-left">
                  <div className="simple-kpi-large">
                    <span>Receita Bruta mensal</span>
                    <strong>R$ {rendaBruta}</strong>
                  </div>
                  <div className="simple-kpi-large">
                    <span>Receita Líquida mensal</span>
                    <strong>R$ {rendaLiquida}</strong>
                  </div>
                </div>

                <div className="simple-chart-right">
                  <div className="chart-title">Receita ao longo dos meses</div>
                  <div className="chart-canvas">
                    <canvas ref={lineRef} />
                  </div>
                </div>
              </div>
            </section>

            {/* Novo card simples: ordens do mês + dois pequenos gráficos */}
            <section className="dashboard-section simple-small-card">
              <div className="small-inner-grid">
                <div className="orders-count">
                  <div className="orders-label">
                    Número de ordens de serviço realizadas no mês
                  </div>
                  <div className="orders-value">{totalOrdemServico}</div>
                </div>

                <div className="small-charts-row">
                  <div className="small-chart-box">
                    <div className="small-chart-title">
                      5 Serviços/combos mais realizados no mês
                    </div>
                    <div className="small-chart">
                      <canvas ref={servicesRef} />
                    </div>
                    <div className="small-chart-legend">
                      <span
                        className="legend-color"
                        style={{ background: "#6c63ff" }}
                      ></span>{" "}
                      Serviços/combos
                    </div>
                  </div>

                  <div className="small-chart-box">
                    <div className="small-chart-title">
                      5 Produtos mais vendidos no mês
                    </div>
                    <div className="small-chart">
                      <canvas ref={productsRef} />
                    </div>
                    <div className="small-chart-legend">
                      <span
                        className="legend-color"
                        style={{ background: "#b388ff" }}
                      ></span>{" "}
                      Produtos
                    </div>
                  </div>
                </div>
              </div>
            </section>

            {/* Último card simples: três KPIs em colunas */}
            <section className="dashboard-section simple-footer-card">
              <div className="footer-grid">
                <div className="footer-item">
                  <div className="footer-label">
                    Receita mensal total gerada por funcionário
                  </div>
                  <div className="footer-value">R${parseFloat(receitaFuncionarioMesAtual || 0).toFixed(2)}</div>
                </div>

                <div className="footer-item">
                  <div className="footer-label">Comissão da proprietária</div>
                  <div className="footer-value">R${parseFloat(proprietorCommission || 0).toFixed(2)}</div>
                </div>

                <div className="footer-item">
                  <div className="footer-label">
                    Número de ordens de serviço realizadas no mês pelos
                    funcionários
                  </div>
                  <div className="footer-value">{quantidadeOrdensServicoFuncionario}</div>
                </div>
              </div>
            </section>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;

