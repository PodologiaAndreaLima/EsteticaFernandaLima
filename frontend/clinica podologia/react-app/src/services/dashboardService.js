import api from './api';

export const dashboardService = {
    getRendaBrutaMesAtual: async () => {
        try {
            const response = await api.get('/dash-simples/renda-bruta-mes-atual');
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar renda bruta do mês atual'
            };
        }
    },

    getRendaLiquidaMesAtual: async () => {
        try {
            const response = await api.get('/dash-simples/renda-liquida-mes-atual');
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar renda liquida do mês atual'
            };
        }
    },

    getRendaBrutaTodosMeses: async () => {
        try {
            const response = await api.get('/dash-simples/renda-bruta-todos-meses');
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar renda bruta de todos os meses'
            };
        }
    },

    getRendaLiquidaTodosMeses: async () => {
        try {
            const response = await api.get('/dash-simples/renda-liquida-todos-meses');  
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar renda liquida de todos os meses'
            };
        }
    },

    getTotalOrdensServicos: async () => {
        try {
            const response = await api.get('/dash-simples/total-ordens-servico');
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar total de ordens de serviços'
            };
        }
    },

    getServicosCombosMaisVendidosMes: async () => {
        try {
            const response = await api.get('/dash-simples/servicos-combos-mais-vendidos-mes');
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar servicos e combos mais vendidos do mês'
            };
        }
    },  

    getProdutosMaisVendidosMes: async () => {
        try {
            const response = await api.get('/dash-simples/produtos-mais-vendidos-mes');
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar produtos mais vendidos do mês'
            };
        }
    },

    getReceitaFuncionarioMesAtual: async () => {
        try {
            const response = await api.get('/dash-simples/receita-funcionario-mes-atual');
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar receita dos funcionários do mês atual'
            };
        }
    },

    getTotalOrdensServicosFuncionarioMesAtual: async () => {
        try {
            const response = await api.get('/dash-simples/quantidade-ordens-servico-mes-atual-funcionario');
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar total de ordens de serviços dos funcionários do mês atual'
            };
        }
    },

    // Novos métodos para dashboard detalhada
    getReceitaBrutaMensal: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/receita-bruta-mensal/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar receita bruta mensal'
            };
        }
    },

    getReceitaLiquidaMensal: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/receita-liquida-mensal/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar receita líquida mensal'
            };
        }
    },

    getCustosFixosMensais: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/custos-fixos-mensais/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar custos fixos mensais'
            };
        }
    },

    getCustosExtrasMensais: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/custos-extras-mensais/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar custos extras mensais'
            };
        }
    },

    getMargemLucroBruto: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/margem-lucro-bruto/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar margem de lucro bruto'
            };
        }
    },

    getVariacaoMensal: async (mesAtual, anoAtual) => {
        try {
            const response = await api.get(`/dash-detalhada/variacao-mensal/${mesAtual}/${anoAtual}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar variação mensal'
            };
        }
    },

    getReceitaAoLongoDosMeses: async (ano) => {
        try {
            const response = await api.get(`/dash-detalhada/receita-ao-longo-dos-meses/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar receita ao longo dos meses'
            };
        }
    },

    getProporcaoCustos: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/proporcao-custos/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar proporção de custos'
            };
        }
    },

    getNumeroOrdensMensal: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/numero-ordens-mensal/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar número de ordens mensal'
            };
        }
    },

    getTicketMedio: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/ticket-medio/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar ticket médio'
            };
        }
    },

    getCustoMedioPorServico: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/custo-medio-por-servico/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar custo médio por serviço'
            };
        }
    },

    getTopServicos: async (mes, ano, limite = 5) => {
        try {
            const response = await api.get(`/dash-detalhada/top-servicos/${mes}/${ano}/${limite}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar top serviços'
            };
        }
    },

    getTopProdutos: async (mes, ano, limite = 5) => {
        try {
            const response = await api.get(`/dash-detalhada/top-produtos/${mes}/${ano}/${limite}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar top produtos'
            };
        }
    },

    getAtendimentosPorDiaSemana: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/atendimentos-por-dia-semana/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar atendimentos por dia da semana'
            };
        }
    },

    getProporcaoAtendimentos: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/proporcao-atendimentos/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar proporção de atendimentos'
            };
        }
    },

    getReceitaPorFuncionario: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/receita-por-funcionario/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar receita por funcionário'
            };
        }
    },

    getComissaoProprietario: async (mes, ano, percentual) => {
        try {
            const response = await api.get(`/dash-detalhada/comissao-proprietario/${mes}/${ano}/${percentual}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar comissão do proprietário'
            };
        }
    },

    getOrdensPorFuncionario: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/ordens-por-funcionario/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar ordens por funcionário'
            };
        }
    },

    getAtendimentosFuncionarioPorDiaSemana: async (mes, ano) => {
        try {
            const response = await api.get(`/dash-detalhada/atendimentos-funcionario-por-dia-semana/${mes}/${ano}`);
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar atendimentos de funcionários por dia da semana'
            };
        }
    },
};