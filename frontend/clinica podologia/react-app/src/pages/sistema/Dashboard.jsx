import React, { useEffect, useRef, useState } from "react";
import "../../styles/Dashboard.css";
import Chart from "chart.js/auto";
import api from "../../services/api";
import { dashboardService } from "../../services/dashboardService";
import microserviceDashboardService from "../../services/microserviceDashboardService";
import usuarioService from "../../services/usuarioService";

const Dashboard = () => {
  // ============ CHART REFERENCES ============
  const lineRef = useRef(null);
  const pieRef = useRef(null);
  const servicesRef = useRef(null);
  const productsRef = useRef(null);
  const detailServicesRef = useRef(null);
  const detailProductsRef = useRef(null);
  const detailBar1Ref = useRef(null);
  const detailBar2Ref = useRef(null);
  const detailDoughRef = useRef(null);
  const finalTopLeftRef = useRef(null);
  const finalTopRightRef = useRef(null);
  const finalBottomRef = useRef(null);

  // ============ VIEW & DATE STATE ============
  const [view, setView] = useState("simples");
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  const [isLoadingDetailedData, setIsLoadingDetailedData] = useState(false);
  const [detailedDataError, setDetailedDataError] = useState(null);
  const [detailedReceitaMeses, setDetailedReceitaMeses] = useState({
    receitaBruta: {},
    receitaLiquida: {},
  });

  // ============ SIMPLE DASHBOARD STATE ============
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
  const [funcionariosDashboard, setFuncionariosDashboard] = useState([]);
  const [selectedFuncionarioId, setSelectedFuncionarioId] = useState("");
  const commissionRate = 0.3; // 30% commission rate

  const toNumber = (value) => Number(value || 0);
  const sumValues = (value) => Object.values(value || {}).reduce((total, item) => total + toNumber(item), 0);
  const normalizeKey = (value) =>
    String(value || "")
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .toLowerCase();
  const getByNormalizedKey = (source, target) => {
    const entry = Object.entries(source || {}).find(([key]) => normalizeKey(key) === target);
    return toNumber(entry?.[1]);
  };
  const getMonthlyValue = (source, month) =>
    Array.isArray(source) ? toNumber(source[month - 1]) : toNumber(source?.[month]);
  const weekDays = [
    { label: "Dom", key: "domingo" },
    { label: "Seg", key: "segunda" },
    { label: "Ter", key: "terca" },
    { label: "Qua", key: "quarta" },
    { label: "Qui", key: "quinta" },
    { label: "Sex", key: "sexta" },
    { label: "Sab", key: "sabado" },
  ];
  const getPreviousPeriod = (month, year) => ({
    month: month === 1 ? 12 : month - 1,
    year: month === 1 ? year - 1 : year,
  });
  const fetchCostTotals = async (month, year) => {
    const [fixedCosts, extraCosts] = await Promise.all([
      api.get(`/dash-detalhada/custos-fixos-mensais/${month}/${year}`),
      api.get(`/dash-detalhada/custos-extras-mensais/${month}/${year}`),
    ]);

    return {
      fixed: toNumber(fixedCosts.data),
      extra: toNumber(extraCosts.data),
    };
  };

  // ============ DETAILED DASHBOARD STATE ============
  const [detailedData, setDetailedData] = useState({
    receitaBruta: 0,
    receitaLiquida: 0,
    custosFixos: 0,
    custosExtras: 0,
    margemLucro: 0,
    variacaoReceita: 0,
    variacaoCustos: 0,
    numeroOrdens: 0,
    ticketMedio: 0,
    custoMedioPorServico: 0,
    proporcaoCustos: {},
    topServicos: [],
    topProdutos: [],
    atendimentosPorDia: {},
    proporcaoAtendimentos: {},
    receitaPorFuncionario: {},
    ordensPorFuncionario: {},
    comissaoProprietario: 0,
    atendimentosFuncionarioPorDia: {},
  });

  // ============ MONTHS ARRAY FOR DISPLAY ============
  const monthNames = [
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
  ];
  const periodLabel = `${monthNames[selectedMonth - 1]}/${String(selectedYear).slice(-2)}`;

  // ============ EFFECT 1: FETCH SIMPLE DASHBOARD DATA (Runs once on mount) ============
  useEffect(() => {
    const fetchSimpleDashboardData = async () => {
      try {
        const [
          resultadoBruta,
          resultadoLiquida,
          resultadoBrutaMeses,
          resultadoLiquidaMeses,
          totalOrdensRes,
          servicosRes,
          produtosRes,
        ] = await Promise.all([
          dashboardService.getRendaBrutaMesAtual(),
          dashboardService.getRendaLiquidaMesAtual(),
          dashboardService.getRendaBrutaTodosMeses(),
          dashboardService.getRendaLiquidaTodosMeses(),
          dashboardService.getTotalOrdensServicos(),
          dashboardService.getServicosCombosMaisVendidosMes(),
          dashboardService.getProdutosMaisVendidosMes(),
        ]);

        if (resultadoBruta.sucess) setRendaBruta(resultadoBruta.data);
        if (resultadoLiquida.sucess) setRendaLiquida(resultadoLiquida.data);
        if (resultadoBrutaMeses.sucess) setRendaBrutaMeses(resultadoBrutaMeses.data);
        if (resultadoLiquidaMeses.sucess) setRendaLiquidaMeses(resultadoLiquidaMeses.data);
        if (totalOrdensRes.sucess) setTotalOrdemServico(totalOrdensRes.data);
        if (servicosRes.sucess) setServicosCombosMaisVendidosMes(servicosRes.data);
        if (produtosRes.sucess) setProdutosMaisVendidosMes(produtosRes.data);
      } catch (error) {
        console.error("Erro ao buscar dados da dashboard simples:", error);
      }
    };

    fetchSimpleDashboardData();
  }, []);

  useEffect(() => {
    const fetchFuncionarios = async () => {
      try {
        const response = await usuarioService.list();
        const usuarios = Array.isArray(response.data) ? response.data : [];
        const funcionarios = usuarios
          .map((usuario) => ({
            id: usuario.id ?? usuario.idUsuario ?? usuario.usuarioId,
            nome: usuario.nomeCompleto ?? usuario.nome ?? usuario.email ?? "Funcionário",
          }))
          .filter((usuario) => usuario.id);

        setFuncionariosDashboard(funcionarios);
        if (!selectedFuncionarioId && funcionarios.length > 0) {
          setSelectedFuncionarioId(String(funcionarios[0].id));
        }
      } catch (error) {
        console.error("Erro ao buscar funcionários da dashboard:", error);
      }
    };

    fetchFuncionarios();
  }, []);

  useEffect(() => {
    if (!selectedFuncionarioId) {
      setReceitaFuncionarioMesAtual(0);
      setProprietorCommission(0);
      setQuantidadeOrdensServicoFuncionario(0);
      return;
    }

    const fetchFuncionarioDashboardData = async () => {
      try {
        const [receitaFuncionarioRes, quantidadeOrdensRes] = await Promise.all([
          dashboardService.getReceitaFuncionarioMesAtual(selectedFuncionarioId),
          dashboardService.getTotalOrdensServicosFuncionarioMesAtual(selectedFuncionarioId),
        ]);

        if (receitaFuncionarioRes.sucess) {
          const receitaFuncionario = toNumber(receitaFuncionarioRes.data);
          setReceitaFuncionarioMesAtual(receitaFuncionario);
          setProprietorCommission(receitaFuncionario * commissionRate);
        }
        if (quantidadeOrdensRes.sucess) {
          setQuantidadeOrdensServicoFuncionario(quantidadeOrdensRes.data);
        }
      } catch (error) {
        console.error("Erro ao buscar dados do funcionário na dashboard simples:", error);
      }
    };

    fetchFuncionarioDashboardData();
  }, [selectedFuncionarioId]);

  // ============ EFFECT 2: FETCH DETAILED DASHBOARD DATA (When switching to detailed or changing month/year) ============
  useEffect(() => {
    if (view !== "detalhada") return;

    const fetchDetailedDashboardData = async () => {
      setIsLoadingDetailedData(true);
      setDetailedDataError(null);

      try {
        const previousPeriod = getPreviousPeriod(selectedMonth, selectedYear);
        const [currentCosts, previousCosts, receitaAnual] = await Promise.all([
          fetchCostTotals(selectedMonth, selectedYear),
          fetchCostTotals(previousPeriod.month, previousPeriod.year),
          api.get(`/dash-detalhada/receita-ao-longo-dos-meses/${selectedYear}`),
        ]);

        const result = await microserviceDashboardService.loadAllDashboardData(
          selectedMonth,
          selectedYear,
          currentCosts.fixed,
          currentCosts.extra,
          previousCosts.fixed,
          previousCosts.extra
        );

        if (result.success) {
          // Extract data from the result
          const data = result.data;
          const variacaoMensal = data.variacaoMensal?.data || {};
          const proporcaoCustos = data.proporcaoCustos?.data || {};
          const atendimentosPorDia = data.atendimentosDiaSemana?.data || {};
          setDetailedReceitaMeses({
            receitaBruta: receitaAnual.data?.receitaBruta || {},
            receitaLiquida: receitaAnual.data?.receitaLiquida || {},
          });
          setDetailedData({
            receitaBruta: data.receitaBruta?.data || 0,
            receitaLiquida: data.receitaLiquida?.data || 0,
            custosFixos: currentCosts.fixed,
            custosExtras: currentCosts.extra,
            margemLucro: data.margemLucro?.data || 0,
            variacaoReceita: variacaoMensal.variacaoReceita || variacaoMensal.receita || 0,
            variacaoCustos: variacaoMensal.variacaoCustos || variacaoMensal.custos || 0,
            numeroOrdens: data.numeroOrdens?.data || 0,
            ticketMedio: data.ticketMedio?.data || 0,
            custoMedioPorServico: data.custoMedioServico?.data || 0,
            proporcaoCustos,
            topServicos: data.topServicos?.data || [],
            topProdutos: data.topProdutos?.data || [],
            atendimentosPorDia,
            proporcaoAtendimentos: data.proporcaoAtendimentos?.data || {},
            receitaPorFuncionario: data.receitaPorUsuario?.data || {},
            ordensPorFuncionario: data.ordensPorUsuario?.data || {},
            comissaoProprietario: data.comissaoProprietario?.data || 0,
            atendimentosFuncionarioPorDia: atendimentosPorDia,
          });
        } else {
          setDetailedDataError(result.error || "Erro ao carregar dados da dashboard detalhada");
          console.error("Erro ao carregar dashboard detalhada:", result.error);
        }
      } catch (error) {
        const errorMessage = `Erro ao buscar dados: ${error.message}`;
        setDetailedDataError(errorMessage);
        console.error(errorMessage, error);
      } finally {
        setIsLoadingDetailedData(false);
      }
    };

    fetchDetailedDashboardData();
  }, [view, selectedMonth, selectedYear]);

  // ============ EFFECT 3: RENDER CHARTS ============
  useEffect(() => {
    const charts = [];

    if (view === "simples") {
      // Validação: verifica se tem pelos menos alguns dados
      const temDadosMeses = Object.keys(rendaBrutaMeses).length > 0 && Object.keys(rendaLiquidaMeses).length > 0;

      if (!temDadosMeses) {
        return;
      }

      // Linha chart: Receita Bruta x Receita Líquida
      if (lineRef.current) {
        try {
          const meses = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
          const dataBruta = meses.map((_, i) => rendaBrutaMeses[i + 1] || 0);
          const dataLiquida = meses.map((_, i) => rendaLiquidaMeses[i + 1] || 0);

          charts.push(
            new Chart(lineRef.current, {
              type: "line",
              data: {
                labels: meses,
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

      // Doughnut chart: Custos
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

      // Bar chart: Serviços/Combos
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

      // Bar chart: Produtos
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
    } else if (view === "detalhada" && detailedData) {
      const meses = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

      if (
        lineRef.current &&
        Object.keys(detailedReceitaMeses.receitaBruta).length > 0 &&
        Object.keys(detailedReceitaMeses.receitaLiquida).length > 0
      ) {
        try {
          charts.push(
            new Chart(lineRef.current, {
              type: "line",
              data: {
                labels: meses,
                datasets: [
                  {
                    label: "Receita Bruta",
                    data: meses.map((_, i) => getMonthlyValue(detailedReceitaMeses.receitaBruta, i + 1)),
                    borderColor: "#6c63ff",
                    backgroundColor: "rgba(108,99,255,0.08)",
                    tension: 0.3,
                    fill: true,
                  },
                  {
                    label: "Receita Líquida",
                    data: meses.map((_, i) => getMonthlyValue(detailedReceitaMeses.receitaLiquida, i + 1)),
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
          console.error("Error creating detail line chart:", err);
        }
      }

      if (pieRef.current) {
        try {
          charts.push(
            new Chart(pieRef.current, {
              type: "doughnut",
              data: {
                labels: ["Fixos", "Extras", "Receita"],
                datasets: [
                  {
                    data: [
                      detailedData.custosFixos,
                      detailedData.custosExtras,
                      Math.max(detailedData.receitaBruta - detailedData.custosFixos - detailedData.custosExtras, 0),
                    ],
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
          console.error("Error creating detail costs chart:", err);
        }
      }

      if (detailServicesRef.current) {
        try {
          charts.push(
            new Chart(detailServicesRef.current, {
              type: "bar",
              data: {
                labels: detailedData.topServicos.map(item => item.nome),
                datasets: [
                  {
                    label: "Serviços/combos",
                    data: detailedData.topServicos.map(item => item.quantidade),
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
          charts.push(
            new Chart(detailProductsRef.current, {
              type: "bar",
              data: {
                labels: detailedData.topProdutos.map(item => item.nome),
                datasets: [
                  {
                    label: "Produtos",
                    data: detailedData.topProdutos.map(item => item.quantidade),
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

      if (detailBar1Ref.current) {
        try {
          charts.push(
            new Chart(detailBar1Ref.current, {
              type: "bar",
              data: {
                labels: weekDays.map(dia => dia.label),
                datasets: [
                  {
                    label: "Quantidade de atendimentos",
                    data: weekDays.map(dia => getByNormalizedKey(detailedData.atendimentosPorDia, dia.key)),
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
            getByNormalizedKey(detailedData.proporcaoAtendimentos, "servicos"),
            getByNormalizedKey(detailedData.proporcaoAtendimentos, "combos"),
            getByNormalizedKey(detailedData.proporcaoAtendimentos, "produtos"),
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

      if (finalTopLeftRef.current) {
        try {
          charts.push(
            new Chart(finalTopLeftRef.current, {
              type: "bar",
              data: {
                labels: detailedData.topServicos.map(item => item.nome),
                datasets: [
                  {
                    data: detailedData.topServicos.map(item => item.quantidade),
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
          charts.push(
            new Chart(finalTopRightRef.current, {
              type: "bar",
              data: {
                labels: detailedData.topProdutos.map(item => item.nome),
                datasets: [
                  {
                    data: detailedData.topProdutos.map(item => item.quantidade),
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
          charts.push(
            new Chart(finalBottomRef.current, {
              type: "bar",
              data: {
                labels: weekDays.map(dia => dia.label),
                datasets: [
                  {
                    data: weekDays.map(dia => getByNormalizedKey(detailedData.atendimentosFuncionarioPorDia, dia.key)),
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
    } else if (false && view === "detalhada" && detailedData) {
      // DETAILED VIEW CHARTS
      
      // Doughnut chart: Proporção de Custos
      if (detailDoughRef.current && Object.keys(detailedData.proporcaoCustos).length > 0) {
        try {
          const labels = Object.keys(detailedData.proporcaoCustos);
          const data = Object.values(detailedData.proporcaoCustos);
          charts.push(
            new Chart(detailDoughRef.current, {
              type: "doughnut",
              data: {
                labels: labels,
                datasets: [
                  {
                    data: data,
                    backgroundColor: ["#6c63ff", "#ff7ab6", "#76e4f7", "#ffa85c"],
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
          console.error("Error creating detail doughnut chart:", err);
        }
      }

      // Bar chart: Top Serviços
      if (detailServicesRef.current && detailedData.topServicos.length > 0) {
        try {
          const labels = detailedData.topServicos.map(s => s.nome);
          const data = detailedData.topServicos.map(s => s.quantidade);
          charts.push(
            new Chart(detailServicesRef.current, {
              type: "bar",
              data: {
                labels: labels,
                datasets: [
                  {
                    label: "Serviços",
                    data: data,
                    backgroundColor: "rgba(108,99,255,0.95)",
                    borderRadius: 6,
                    borderSkipped: false,
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
                responsive: true,
              },
            })
          );
        } catch (err) {
          console.error("Error creating detail services chart:", err);
        }
      }

      // Bar chart: Top Produtos
      if (detailProductsRef.current && detailedData.topProdutos.length > 0) {
        try {
          const labels = detailedData.topProdutos.map(p => p.nome);
          const data = detailedData.topProdutos.map(p => p.quantidade);
          charts.push(
            new Chart(detailProductsRef.current, {
              type: "bar",
              data: {
                labels: labels,
                datasets: [
                  {
                    label: "Produtos",
                    data: data,
                    backgroundColor: "rgba(179,136,255,0.95)",
                    borderRadius: 6,
                    borderSkipped: false,
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
                responsive: true,
              },
            })
          );
        } catch (err) {
          console.error("Error creating detail products chart:", err);
        }
      }

      // Bar chart: Atendimentos por dia da semana
      if (detailBar1Ref.current && Object.keys(detailedData.atendimentosDiaSemana).length > 0) {
        try {
          const labels = Object.keys(detailedData.atendimentosDiaSemana);
          const data = Object.values(detailedData.atendimentosDiaSemana);
          charts.push(
            new Chart(detailBar1Ref.current, {
              type: "bar",
              data: {
                labels: labels,
                datasets: [
                  {
                    label: "Atendimentos",
                    data: data,
                    backgroundColor: "rgba(255,122,182,0.95)",
                    borderRadius: 6,
                    borderSkipped: false,
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
                responsive: true,
              },
            })
          );
        } catch (err) {
          console.error("Error creating detail bar1 chart:", err);
        }
      }

      // Pie chart: Proporção de Atendimentos
      if (detailBar2Ref.current && Object.keys(detailedData.proporcaoAtendimentos).length > 0) {
        try {
          const labels = Object.keys(detailedData.proporcaoAtendimentos);
          const data = Object.values(detailedData.proporcaoAtendimentos);
          charts.push(
            new Chart(detailBar2Ref.current, {
              type: "pie",
              data: {
                labels: labels,
                datasets: [
                  {
                    data: data,
                    backgroundColor: ["#6c63ff", "#ff7ab6", "#76e4f7", "#ffa85c", "#53d4ac"],
                  },
                ],
              },
              options: {
                plugins: {
                  legend: {
                    position: "right",
                    labels: { usePointStyle: true, boxWidth: 12, padding: 12 },
                  },
                },
                maintainAspectRatio: false,
              },
            })
          );
        } catch (err) {
          console.error("Error creating detail bar2 chart:", err);
        }
      }
    }

    // Force a resize/update after render so charts appear correctly
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
      charts.forEach((c) => {
        try {
          c.destroy();
        } catch (e) {
          // ignore
        }
      });
    };
  }, [
    view,
    rendaBrutaMeses,
    rendaLiquidaMeses,
    servicosCombosMaisVendidosMes,
    produtosMaisVendidosMes,
    detailedReceitaMeses,
    detailedData,
  ]);

  // ============ RENDER ============
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
                className={`btn-secondary ${view === "simples" ? "active" : ""}`}
                onClick={() => setView("simples")}
              >
                Simples
              </button>
              <button
                className={`btn-primary ${view === "detalhada" ? "active" : ""}`}
                onClick={() => setView("detalhada")}
              >
                Detalhada
              </button>
            </div>
            <div className="period">
              Período atual
              <br />
              <strong>{periodLabel}</strong>
            </div>
          </div>
        </div>

        {/* SIMPLE DASHBOARD VIEW */}
        {view === "simples" && (
          <div className="dashboard-grid simple-grid">
            <div className="simple-employee-filter">
              <label htmlFor="dashboard-funcionario">Funcionário</label>
              <select
                id="dashboard-funcionario"
                value={selectedFuncionarioId}
                onChange={(event) => setSelectedFuncionarioId(event.target.value)}
              >
                {funcionariosDashboard.length === 0 && (
                  <option value="">Nenhum funcionário encontrado</option>
                )}
                {funcionariosDashboard.map((funcionario) => (
                  <option key={funcionario.id} value={funcionario.id}>
                    {funcionario.nome}
                  </option>
                ))}
              </select>
            </div>

            <section className="dashboard-section simple-card metrics-card">
              <div className="simple-inner-grid">
                <div className="simple-kpis-left">
                  <div className="simple-kpi-large">
                    <span>Receita bruta mensal</span>
                    <strong>R$ {rendaBruta}</strong>
                  </div>
                  <div className="simple-kpi-large">
                    <span>Receita líquida mensal</span>
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

            <section className="dashboard-section simple-small-card">
              <div className="small-inner-grid">
                <div className="orders-count">
                  <div className="orders-label">Total de ordens</div>
                  <div className="orders-value">{totalOrdemServico}</div>
                </div>
                <div className="small-charts-row">
                  <div className="small-chart-box">
                    <div className="small-chart">
                      <canvas ref={servicesRef} />
                    </div>
                  </div>
                  <div className="small-chart-box">
                    <div className="small-chart">
                      <canvas ref={productsRef} />
                    </div>
                  </div>
                </div>
              </div>
            </section>

            <section className="dashboard-section simple-footer-card">
              <div className="footer-grid">
                <div className="footer-item">
                  <div className="footer-label">Receita por Funcionário</div>
                  <div className="footer-value">R$ {parseFloat(receitaFuncionarioMesAtual || 0).toFixed(2)}</div>
                </div>
                <div className="footer-item">
                  <div className="footer-label">Comissão da Proprietária</div>
                  <div className="footer-value">R$ {parseFloat(proprietorCommission || 0).toFixed(2)}</div>
                </div>
                <div className="footer-item">
                  <div className="footer-label">Ordens de serviço</div>
                  <div className="footer-value">{quantidadeOrdensServicoFuncionario}</div>
                </div>
              </div>
            </section>
          </div>
        )}

        {/* DETAILED DASHBOARD VIEW */}
        {view === "detalhada" && (
          <div className="dashboard-grid top-grid">
            {isLoadingDetailedData ? (
              <div style={{ padding: "40px", textAlign: "center", color: "#666" }}>
                Carregando...
              </div>
            ) : detailedDataError ? (
              <div style={{ padding: "40px", textAlign: "center", color: "#d32f2f" }}>
                {detailedDataError}
              </div>
            ) : (
              <>
                <section className="dashboard-section metrics-card detailed-summary-card">
                  <div className="detailed-summary-layout">
                    <div className="detailed-summary-kpis">
                          <div className="metric-item">
                            <span>Receita bruta mensal</span>
                            <strong>R$ {toNumber(detailedData.receitaBruta).toFixed(2)}</strong>
                          </div>
                          <div className="metric-item">
                            <span>Receita líquida mensal</span>
                            <strong>R$ {toNumber(detailedData.receitaLiquida).toFixed(2)}</strong>
                          </div>
                          <div className="metric-item">
                            <span>Custo fixo mensal</span>
                            <strong>R$ {toNumber(detailedData.custosFixos).toFixed(2)}</strong>
                          </div>
                          <div className="metric-item">
                            <span>Custo extra total do mês</span>
                            <strong>R$ {toNumber(detailedData.custosExtras).toFixed(2)}</strong>
                          </div>
                          <div className="metric-item variation-box summary-variation-box">
                            <div className="variation-caption">
                              Variação em relação ao
                              <br />
                              mês anterior
                            </div>
                            <div className="variation-values summary-variation-values">
                              <div className="variation-receita summary-variation-item">
                                <strong>{detailedData.variacaoReceita >= 0 ? "+" : ""}{toNumber(detailedData.variacaoReceita).toFixed(1)}%</strong>
                                <span>Receita</span>
                              </div>
                              <div className="variation-custo summary-variation-item">
                                <strong>{detailedData.variacaoCustos >= 0 ? "+" : ""}{toNumber(detailedData.variacaoCustos).toFixed(1)}%</strong>
                                <span>Custos</span>
                              </div>
                            </div>
                          </div>
                          <div className="metric-item gross-margin-box">
                            <div className="gross-margin">
                              Margem de lucro bruto
                              <br />
                              <strong>{toNumber(detailedData.margemLucro).toFixed(1)}%</strong>
                            </div>
                          </div>
                    </div>
                    <div className="detailed-summary-charts">
                        <div className="line-chart">
                          <div className="chart-title">Receita ao longo dos meses</div>
                          <canvas ref={lineRef} />
                        </div>
                        <div className="doughnut-chart">
                          <div className="chart-title">Proporção de custos do mês</div>
                          <canvas ref={pieRef} />
                        </div>
                    </div>
                  </div>
                </section>

                <section className="dashboard-section detailed-extra-card">
                  <div className="detail-extra-inner">
                    <div className="detail-kpis-left">
                      <div className="kpi-item">
                        <div className="kpi-label">Número de ordens de serviço realizadas no mês</div>
                        <div className="kpi-value">{detailedData.numeroOrdens}</div>
                      </div>
                      <div className="kpi-item">
                        <div className="kpi-label">Média de valor gasto por clientes (ticket médio)</div>
                        <div className="kpi-value">R${toNumber(detailedData.ticketMedio).toFixed(2)}</div>
                      </div>
                      <div className="kpi-item">
                        <div className="kpi-label">Custo médio por serviço</div>
                        <div className="kpi-value">R${toNumber(detailedData.custoMedioPorServico).toFixed(2)}</div>
                      </div>
                    </div>

                    <div className="detail-charts-wrapper">
                      <div className="detail-charts-grid detail-grid-2x2">
                        <div className="detail-chart-box">
                          <div className="detail-chart-title">5 Serviços/combos mais realizados no mês</div>
                          <div className="detail-chart-canvas">
                            <canvas ref={detailServicesRef} />
                          </div>
                          <div className="detail-chart-legend">
                            <span className="legend-color" style={{ background: "#6c63ff" }}></span>{" "}
                            Serviços/combos
                          </div>
                        </div>
                        <div className="detail-chart-box">
                          <div className="detail-chart-title">5 Produtos mais vendidos no mês</div>
                          <div className="detail-chart-canvas">
                            <canvas ref={detailProductsRef} />
                          </div>
                          <div className="detail-chart-legend">
                            <span className="legend-color" style={{ background: "#b388ff" }}></span>{" "}
                            Produtos
                          </div>
                        </div>
                        <div className="detail-chart-box">
                          <div className="detail-chart-title">Quantidade de atendimentos por dia da semana</div>
                          <div className="detail-chart-canvas">
                            <canvas ref={detailBar1Ref} />
                          </div>
                          <div className="detail-chart-legend">
                            <span className="legend-color" style={{ background: "#6c63ff" }}></span>{" "}
                            Quantidade de atendimentos
                          </div>
                        </div>
                        <div className="detail-chart-box doughnut-side">
                          <div className="detail-chart-title">Proporção de atendimentos realizados no mês</div>
                          <div className="detail-dough-canvas">
                            <canvas ref={detailDoughRef} />
                          </div>
                          <div className="detail-chart-legend doughnut-legend">
                            <div className="legend-row">
                              <span className="legend-color" style={{ background: "#6c63ff" }}></span>{" "}
                              Serviços
                            </div>
                            <div className="legend-row">
                              <span className="legend-color" style={{ background: "#ff7ab6" }}></span>{" "}
                              Combos
                            </div>
                            <div className="legend-row">
                              <span className="legend-color" style={{ background: "#76e4f7" }}></span>{" "}
                              Produtos
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </section>

                <section className="dashboard-section final-detailed-card">
                  <div className="final-inner">
                    <div className="final-kpis-left">
                      <div className="kpi-item">
                        <div className="kpi-label">Receita mensal total gerada por funcionário</div>
                        <div className="kpi-value">R${sumValues(detailedData.receitaPorFuncionario).toFixed(2)}</div>
                      </div>
                      <div className="kpi-item">
                        <div className="kpi-label">Comissão da proprietária</div>
                        <div className="kpi-value">R${toNumber(detailedData.comissaoProprietario).toFixed(2)}</div>
                      </div>
                      <div className="kpi-item">
                        <div className="kpi-label">Número de ordens de serviço realizadas no mês pelos funcionários</div>
                        <div className="kpi-value">{sumValues(detailedData.ordensPorFuncionario)}</div>
                      </div>
                    </div>

                    <div className="final-charts-wrapper">
                      <div className="final-charts-grid">
                        <div className="final-chart-box">
                          <div className="final-chart-title">5 Serviços/combos mais realizados no mês por funcionários</div>
                          <div className="final-chart-canvas">
                            <canvas ref={finalTopLeftRef} />
                          </div>
                        </div>
                        <div className="final-chart-box">
                          <div className="final-chart-title">5 Produtos mais realizados no mês por funcionários</div>
                          <div className="final-chart-canvas">
                            <canvas ref={finalTopRightRef} />
                          </div>
                        </div>
                        <div className="final-chart-box final-chart-bottom">
                          <div className="final-chart-title">Quantidade de atendimentos de funcionários por dia da semana</div>
                          <div className="final-chart-canvas">
                            <canvas ref={finalBottomRef} />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </section>

                {false && (
                  <>
                {/* TOP KPI CARDS */}
                <section className="dashboard-section simple-card metrics-card">
                  <div className="simple-inner-grid" style={{ display: "grid", gridTemplateColumns: "repeat(6, 1fr)", gap: "16px" }}>
                    <div style={{ padding: "16px", borderRadius: "8px", backgroundColor: "rgba(108,99,255,0.08)" }}>
                      <div style={{ fontSize: "12px", color: "#666", marginBottom: "8px" }}>Receita Bruta</div>
                      <div style={{ fontSize: "18px", fontWeight: "600", color: "#6c63ff" }}>R$ {parseFloat(detailedData.receitaBruta || 0).toFixed(2)}</div>
                    </div>
                    <div style={{ padding: "16px", borderRadius: "8px", backgroundColor: "rgba(255,122,182,0.08)" }}>
                      <div style={{ fontSize: "12px", color: "#666", marginBottom: "8px" }}>Receita Líquida</div>
                      <div style={{ fontSize: "18px", fontWeight: "600", color: "#ff7ab6" }}>R$ {parseFloat(detailedData.receitaLiquida || 0).toFixed(2)}</div>
                    </div>
                    <div style={{ padding: "16px", borderRadius: "8px", backgroundColor: "rgba(118,228,247,0.08)" }}>
                      <div style={{ fontSize: "12px", color: "#666", marginBottom: "8px" }}>Margem Lucro</div>
                      <div style={{ fontSize: "18px", fontWeight: "600", color: "#76e4f7" }}>{parseFloat(detailedData.margemLucro || 0).toFixed(1)}%</div>
                    </div>
                    <div style={{ padding: "16px", borderRadius: "8px", backgroundColor: "rgba(255,168,92,0.08)" }}>
                      <div style={{ fontSize: "12px", color: "#666", marginBottom: "8px" }}>Nº Ordens</div>
                      <div style={{ fontSize: "18px", fontWeight: "600", color: "#ffa85c" }}>{detailedData.numeroOrdens || 0}</div>
                    </div>
                    <div style={{ padding: "16px", borderRadius: "8px", backgroundColor: "rgba(83,212,172,0.08)" }}>
                      <div style={{ fontSize: "12px", color: "#666", marginBottom: "8px" }}>Ticket Médio</div>
                      <div style={{ fontSize: "18px", fontWeight: "600", color: "#53d4ac" }}>R$ {parseFloat(detailedData.ticketMedio || 0).toFixed(2)}</div>
                    </div>
                    <div style={{ padding: "16px", borderRadius: "8px", backgroundColor: "rgba(200,200,200,0.08)" }}>
                      <div style={{ fontSize: "12px", color: "#666", marginBottom: "8px" }}>Variação Mensal</div>
                      <div style={{ fontSize: "18px", fontWeight: "600", color: "#999" }}>{parseFloat(detailedData.variacaoMensal || 0).toFixed(1)}%</div>
                    </div>
                  </div>
                </section>

                {/* CHARTS ROW 1 */}
                <section className="dashboard-section simple-card" style={{ gridColumn: "span 2" }}>
                  <div style={{ padding: "16px" }}>
                    <div style={{ fontSize: "14px", fontWeight: "600", marginBottom: "12px" }}>Proporção de Custos</div>
                    <div style={{ height: "250px" }}>
                      <canvas ref={detailDoughRef} />
                    </div>
                  </div>
                </section>

                <section className="dashboard-section simple-card" style={{ gridColumn: "span 2" }}>
                  <div style={{ padding: "16px" }}>
                    <div style={{ fontSize: "14px", fontWeight: "600", marginBottom: "12px" }}>Top Serviços</div>
                    <div style={{ height: "250px" }}>
                      <canvas ref={detailServicesRef} />
                    </div>
                  </div>
                </section>

                <section className="dashboard-section simple-card" style={{ gridColumn: "span 2" }}>
                  <div style={{ padding: "16px" }}>
                    <div style={{ fontSize: "14px", fontWeight: "600", marginBottom: "12px" }}>Top Produtos</div>
                    <div style={{ height: "250px" }}>
                      <canvas ref={detailProductsRef} />
                    </div>
                  </div>
                </section>

                {/* CHARTS ROW 2 */}
                <section className="dashboard-section simple-card" style={{ gridColumn: "span 2" }}>
                  <div style={{ padding: "16px" }}>
                    <div style={{ fontSize: "14px", fontWeight: "600", marginBottom: "12px" }}>Atendimentos por Dia</div>
                    <div style={{ height: "250px" }}>
                      <canvas ref={detailBar1Ref} />
                    </div>
                  </div>
                </section>

                <section className="dashboard-section simple-card" style={{ gridColumn: "span 2" }}>
                  <div style={{ padding: "16px" }}>
                    <div style={{ fontSize: "14px", fontWeight: "600", marginBottom: "12px" }}>Proporção Atendimentos</div>
                    <div style={{ height: "250px" }}>
                      <canvas ref={detailBar2Ref} />
                    </div>
                  </div>
                </section>

                {/* COMMISSION FOOTER */}
                <section className="dashboard-section simple-footer-card">
                  <div className="footer-grid">
                    <div className="footer-item">
                      <div className="footer-label">Custo Médio Serviço</div>
                      <div className="footer-value">R$ {parseFloat(detailedData.custoMedioServico || 0).toFixed(2)}</div>
                    </div>
                    <div className="footer-item">
                      <div className="footer-label">Comissão Proprietária</div>
                      <div className="footer-value">R$ {parseFloat(detailedData.comissaoProprietario || 0).toFixed(2)}</div>
                    </div>
                    <div className="footer-item">
                      <div className="footer-label">Total de Usuários</div>
                      <div className="footer-value">{detailedData.receitaPorUsuario?.length || 0}</div>
                    </div>
                  </div>
                </section>
                  </>
                )}
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
