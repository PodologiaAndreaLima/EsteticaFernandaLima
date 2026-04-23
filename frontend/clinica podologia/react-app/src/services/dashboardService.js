import api from './api';

/**
 * Serviço de Dashboard Simples
 * 
 * Este serviço fornece acesso aos dados da dashboard simples,
 * que é calculada no backend principal (esteticaFernandaLima).
 * 
 * A dashboard simples fornece uma visão geral básica do mês atual.
 * Para dados mais detalhados e com suporte a filtro de mês/ano,
 * utilize o microserviceDashboardService.
 * 
 *  microserviceDashboardService - Para dashboard detalhada (microserviço)
 */
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

    getReceitaFuncionarioMesAtual: async (funcionarioId) => {
        try {
            const params = funcionarioId ? { funcionarioId } : {};
            const response = await api.get('/dash-simples/receita-funcionario-mes-atual', { params });
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

    getReceitaComissaoProprietariaMesAtual: async (proprietarioId = 1) => {
        try {
            const response = await api.get('/dash-simples/receita-comissao-proprietaria-mes-atual', {
                params: { proprietarioId }
            });
            return {
                sucess: true,
                data: response.data
            };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Erro ao buscar receita comissionável da proprietária'
            };
        }
    },

    getTotalOrdensServicosFuncionarioMesAtual: async (funcionarioId) => {
        try {
            const params = funcionarioId ? { funcionarioId } : {};
            const response = await api.get('/dash-simples/quantidade-ordens-servico-mes-atual-funcionario', { params });
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
};
