import React, { useEffect, useRef, useState } from "react";
import "../../styles/Dashboard.css";
import Chart from "chart.js/auto";

const Dashboard = () => {
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
  const [view, setView] = useState("detalhada");

  useEffect(() => {
    console.log("Dashboard mounted");
    const charts = [];

    if (lineRef.current) {
      try {
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
                  data: [50, 60, 55, 70, 65, 80, 90, 85, 88, 95, 92, 100],
                  borderColor: "#6c63ff",
                  backgroundColor: "rgba(108,99,255,0.08)",
                  tension: 0.3,
                  fill: true,
                },
                {
                  label: "Receita Líquida",
                  data: [40, 48, 50, 60, 58, 70, 75, 72, 76, 82, 80, 90],
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
        charts.push(
          new Chart(servicesRef.current, {
            type: "bar",
            data: {
              labels: ["A", "B", "C", "D", "E"],
              datasets: [
                {
                  label: "Serviços/combos",
                  data: [60, 20, 30, 40, 55],
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
              labels: ["A", "B", "C", "D", "E"],
              datasets: [
                {
                  label: "Produtos",
                  data: [30, 45, 25, 35, 40],
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

    // additional charts used on the detailed extra card: services & products (top row)
    if (detailServicesRef.current) {
      try {
        charts.push(
          new Chart(detailServicesRef.current, {
            type: "bar",
            data: {
              labels: ["A", "B", "C", "D", "E"],
              datasets: [
                {
                  label: "Serviços/combos",
                  data: [60, 20, 30, 40, 55],
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
              labels: ["A", "B", "C", "D", "E"],
              datasets: [
                {
                  label: "Produtos",
                  data: [30, 45, 25, 35, 40],
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
        charts.push(
          new Chart(detailBar1Ref.current, {
            type: "bar",
            data: {
              labels: ["Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"],
              datasets: [
                {
                  label: "Qtde atendimentos",
                  data: [10, 35, 50, 45, 60, 70, 20],
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

    if (detailBar2Ref.current) {
      try {
        charts.push(
          new Chart(detailBar2Ref.current, {
            type: "bar",
            data: {
              labels: ["A", "B", "C", "D", "E"],
              datasets: [
                {
                  label: "Faturamento por categoria",
                  data: [20, 40, 25, 35, 45],
                  backgroundColor: "rgba(179,136,255,0.95)",
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
        console.error("Error creating detail bar2 chart:", err);
      }
    }

    if (detailDoughRef.current) {
      try {
        charts.push(
          new Chart(detailDoughRef.current, {
            type: "doughnut",
            data: {
              labels: ["Serviços", "Combos", "Produtos"],
              datasets: [
                {
                  data: [65, 15, 20],
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
        charts.push(
          new Chart(finalTopLeftRef.current, {
            type: "bar",
            data: {
              labels: ["A", "B", "C", "D", "E"],
              datasets: [
                {
                  data: [40, 20, 30, 25, 50],
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
              labels: ["A", "B", "C", "D", "E"],
              datasets: [
                {
                  data: [30, 45, 20, 35, 40],
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
              labels: ["Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"],
              datasets: [
                {
                  data: [12, 55, 40, 30, 65, 80, 22],
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

    // somente os dois charts principais (linha e doughnut)

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
  }, [view]);

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
                className={`btn-secondary ${
                  view === "simples" ? "active" : ""
                }`}
                onClick={() => setView("simples")}
              >
                Simples
              </button>
              <button
                className={`btn-primary ${
                  view === "detalhada" ? "active" : ""
                }`}
                onClick={() => setView("detalhada")}
              >
                Detalhada
              </button>
            </div>
            <div className="period">
              Período atual
              <br />
              <strong>Novembro/25</strong>
            </div>
          </div>
        </div>

        {/* Render da view detalhada */}
        {view === "detalhada" && (
          <div className="dashboard-grid top-grid">
            <section className="dashboard-section metrics-card">
              <div className="metrics-row vertical">
                <div className="metrics-top">
                  <div className="metrics-left-col">
                    <div className="metrics-list">
                      <div className="metric-item">
                        <span>Receita Bruta mensal</span>
                        <strong>R$100,00</strong>
                      </div>
                      <div className="metric-item">
                        <span>Receita Líquida mensal</span>
                        <strong>R$100,00</strong>
                      </div>
                      <div className="metric-item">
                        <span>Custo Fixo mensal</span>
                        <strong>R$100,00</strong>
                      </div>
                      <div className="metric-item">
                        <span>Custo Extra total do mês</span>
                        <strong>R$100,00</strong>
                      </div>
                    </div>

                    <div className="variation-left">
                      <div className="variation-caption">
                        Variação em relação ao mês anterior
                      </div>
                      <div className="variation-receita">
                        +12% <span>Receita</span>
                      </div>
                      <div className="variation-custo">
                        -5% <span>Custos</span>
                      </div>
                    </div>
                  </div>

                  <div className="variation-right">
                    <div className="gross-margin">
                      Margem de Lucro Bruto
                      <br />
                      <strong>40%</strong>
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
                    <div className="kpi-value">20</div>
                  </div>

                  <div className="kpi-item">
                    <div className="kpi-label">
                      Média de valor gasto por clientes (Ticket Médio)
                    </div>
                    <div className="kpi-value">R$100,00</div>
                  </div>

                  <div className="kpi-item">
                    <div className="kpi-label">Custo médio por serviço</div>
                    <div className="kpi-value">R$100,00</div>
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
                        5 Produtos mais realizados no mês
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
                    <div className="kpi-value">R$100,00</div>
                  </div>

                  <div className="kpi-item">
                    <div className="kpi-label">Comissão da proprietária</div>
                    <div className="kpi-value">R$100,00</div>
                  </div>

                  <div className="kpi-item">
                    <div className="kpi-label">
                      Número de ordens de serviço realizadas no mês pelos
                      funcionários
                    </div>
                    <div className="kpi-value">20</div>
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
                    <strong>R$100,00</strong>
                  </div>
                  <div className="simple-kpi-large">
                    <span>Receita Líquida mensal</span>
                    <strong>R$100,00</strong>
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
                  <div className="orders-value">20</div>
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
                      5 Produtos mais realizados no mês
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
                  <div className="footer-value">R$100,00</div>
                </div>

                <div className="footer-item">
                  <div className="footer-label">Comissão da proprietária</div>
                  <div className="footer-value">R$100,00</div>
                </div>

                <div className="footer-item">
                  <div className="footer-label">
                    Número de ordens de serviço realizadas no mês pelos
                    funcionários
                  </div>
                  <div className="footer-value">20</div>
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
