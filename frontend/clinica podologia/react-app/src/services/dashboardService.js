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
    }
}