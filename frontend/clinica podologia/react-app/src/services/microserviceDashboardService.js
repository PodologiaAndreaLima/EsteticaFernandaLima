import apiMs from './apiMs';

/**
 * Serviço de Dashboard Detalhada - Microserviço de Ordem de Serviço
 * 
 * Este serviço fornece acesso aos dados da dashboard detalhada,
 * que são calculados e armazenados no microserviço (ordem-servico-ms).
 * 
 * A dashboard detalhada processa eventos via RabbitMQ e fornece métricas
 * muito mais ricas que a dashboard simples.
 */

const handleError = (errorMessage) => (error) => {
  return {
    success: false,
    error: error.response?.data?.message || errorMessage,
  };
};

export const microserviceDashboardService = {
  // ============ MÉTRICAS DE RECEITA ============

  /**
   * Obtém a receita bruta para um mês/ano específico
   */
  getReceitaBruta: async (mes, ano) => {
    try {
      const response = await apiMs.get(`/dashboard/receita-bruta/${mes}/${ano}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar receita bruta de ${mes}/${ano}`)(error);
    }
  },

  /**
   * Obtém a receita líquida (receita bruta - custos) para um mês/ano
   * @param custoFixo Custo fixo total para o período
   * @param custoExtra Custo extra total para o período
   */
  getReceitaLiquida: async (mes, ano, custoFixo = 0, custoExtra = 0) => {
    try {
      const response = await apiMs.get(
        `/dashboard/receita-liquida/${mes}/${ano}?custosFixos=${custoFixo}&custosExtras=${custoExtra}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar receita líquida de ${mes}/${ano}`)(error);
    }
  },

  /**
   * Obtém a margem de lucro (em percentual) para um mês/ano
   */
  getMargemLucro: async (mes, ano, custoFixo = 0, custoExtra = 0) => {
    try {
      const response = await apiMs.get(
        `/dashboard/margem-lucro/${mes}/${ano}?custosFixos=${custoFixo}&custosExtras=${custoExtra}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar margem de lucro de ${mes}/${ano}`)(error);
    }
  },

  // ============ MÉTRICAS DE PEDIDOS ============

  /**
   * Obtém o número total de ordens de serviço para um mês/ano
   */
  getNumeroOrdens: async (mes, ano) => {
    try {
      const response = await apiMs.get(`/dashboard/numero-ordens/${mes}/${ano}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar número de ordens de ${mes}/${ano}`)(error);
    }
  },

  /**
   * Obtém o ticket médio (valor médio por ordem) para um mês/ano
   */
  getTicketMedio: async (mes, ano) => {
    try {
      const response = await apiMs.get(`/dashboard/ticket-medio/${mes}/${ano}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar ticket médio de ${mes}/${ano}`)(error);
    }
  },

  /**
   * Obtém o custo médio por serviço para um mês/ano
   */
  getCustoMedioServico: async (mes, ano, custoFixo = 0, custoExtra = 0) => {
    try {
      const response = await apiMs.get(
        `/dashboard/custo-medio-servico/${mes}/${ano}?custosFixos=${custoFixo}&custosExtras=${custoExtra}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar custo médio de serviço de ${mes}/${ano}`)(error);
    }
  },

  // ============ TENDÊNCIAS E SÉRIES ============

  /**
   * Obtém a variação mensal (comparação com mês anterior) para um mês/ano
   */
  getVariacaoMensal: async (
    mes,
    ano,
    custoFixoAtual = 0,
    custoExtraAtual = 0,
    custoFixoAnterior = 0,
    custoExtraAnterior = 0
  ) => {
    try {
      const response = await apiMs.get(
        `/dashboard/variacao-mensal/${mes}/${ano}?custosFixosAtual=${custoFixoAtual}&custosExtrasAtual=${custoExtraAtual}&custosFixosAnterior=${custoFixoAnterior}&custosExtrasAnterior=${custoExtraAnterior}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar variação mensal de ${mes}/${ano}`)(error);
    }
  },

  /**
   * Obtém a receita anual (por mês) para um ano específico
   * Retorna um mapa com receita de cada mês do ano
   */
  getReceitaAnual: async (ano, custosFixos = {}, custosExtras = {}) => {
    try {
      const response = await apiMs.post(`/dashboard/receita-anual/${ano}`, {
        custosFixos,
        custosExtras,
      });
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar receita anual de ${ano}`)(error);
    }
  },

  /**
   * Obtém a proporção de custos (percentual de cada tipo de custo) para um mês/ano
   */
  getProporcaoCustos: async (mes, ano, custoFixo = 0, custoExtra = 0) => {
    try {
      const response = await apiMs.get(
        `/dashboard/proporcao-custos/${mes}/${ano}?custosFixos=${custoFixo}&custosExtras=${custoExtra}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(`Erro ao buscar proporção de custos de ${mes}/${ano}`)(error);
    }
  },

  // ============ TOP ITENS ============

  /**
   * Obtém os top N serviços mais vendidos para um mês/ano
   * @param limite Número máximo de serviços a retornar (padrão: 5)
   */
  getTopServicos: async (mes, ano, limite = 5) => {
    try {
      const response = await apiMs.get(
        `/dashboard/top-servicos/${mes}/${ano}?limite=${limite}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(
        `Erro ao buscar top ${limite} serviços de ${mes}/${ano}`
      )(error);
    }
  },

  /**
   * Obtém os top N produtos mais vendidos para um mês/ano
   * @param limite Número máximo de produtos a retornar (padrão: 5)
   */
  getTopProdutos: async (mes, ano, limite = 5) => {
    try {
      const response = await apiMs.get(
        `/dashboard/top-produtos/${mes}/${ano}?limite=${limite}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(
        `Erro ao buscar top ${limite} produtos de ${mes}/${ano}`
      )(error);
    }
  },

  // ============ DISTRIBUIÇÃO E ANÁLISE ============

  /**
   * Obtém a distribuição de atendimentos por dia da semana para um mês/ano
   * Retorna contagem de atendimentos para cada dia (Segunda a Domingo)
   */
  getAtendimentosDiaSemana: async (mes, ano) => {
    try {
      const response = await apiMs.get(
        `/dashboard/atendimentos-dia-semana/${mes}/${ano}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(
        `Erro ao buscar atendimentos por dia da semana de ${mes}/${ano}`
      )(error);
    }
  },

  /**
   * Obtém a proporção de atendimentos (serviço vs produto vs combo) para um mês/ano
   * Retorna percentual de cada tipo dentro do total de atendimentos
   */
  getProporcaoAtendimentos: async (mes, ano) => {
    try {
      const response = await apiMs.get(
        `/dashboard/proporcao-atendimentos/${mes}/${ano}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(
        `Erro ao buscar proporção de atendimentos de ${mes}/${ano}`
      )(error);
    }
  },

  // ============ PERFORMANCE POR USUÁRIO/FUNCIONÁRIO ============

  getReceitaPorUsuario: async (mes, ano) => {
    try {
      const response = await apiMs.get(`/dashboard/receita-por-usuario/${mes}/${ano}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(
        `Erro ao buscar receita por usuário de ${mes}/${ano}`
      )(error);
    }
  },


  getOrdensPorUsuario: async (mes, ano) => {
    try {
      const response = await apiMs.get(`/dashboard/ordens-por-usuario/${mes}/${ano}`);
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(
        `Erro ao buscar ordens por usuário de ${mes}/${ano}`
      )(error);
    }
  },


  getComissaoProprietario: async (mes, ano, percentual = 30, custoFixo = 0, custoExtra = 0) => {
    try {
      const response = await apiMs.get(
        `/dashboard/comissao-proprietario/${mes}/${ano}/${percentual}?custosFixos=${custoFixo}&custosExtras=${custoExtra}`
      );
      return {
        success: true,
        data: response.data,
      };
    } catch (error) {
      return handleError(
        `Erro ao buscar comissão da proprietária de ${mes}/${ano}`
      )(error);
    }
  },

  // ============ METODO AUXILIAR PARA CARREGAR TODOS OS DADOS ============

  loadAllDashboardData: async (
    mes,
    ano,
    custoFixo = 0,
    custoExtra = 0,
    custoFixoAnterior = 0,
    custoExtraAnterior = 0
  ) => {
    try {
      const [
        receitaBruta,
        receitaLiquida,
        margemLucro,
        numeroOrdens,
        ticketMedio,
        custoMedioServico,
        variacaoMensal,
        proporcaoCustos,
        topServicos,
        topProdutos,
        atendimentosDiaSemana,
        proporcaoAtendimentos,
        receitaPorUsuario,
        ordensPorUsuario,
        comissaoProprietario,
      ] = await Promise.all([
        microserviceDashboardService.getReceitaBruta(mes, ano),
        microserviceDashboardService.getReceitaLiquida(mes, ano, custoFixo, custoExtra),
        microserviceDashboardService.getMargemLucro(mes, ano, custoFixo, custoExtra),
        microserviceDashboardService.getNumeroOrdens(mes, ano),
        microserviceDashboardService.getTicketMedio(mes, ano),
        microserviceDashboardService.getCustoMedioServico(mes, ano, custoFixo, custoExtra),
        microserviceDashboardService.getVariacaoMensal(
          mes,
          ano,
          custoFixo,
          custoExtra,
          custoFixoAnterior,
          custoExtraAnterior
        ),
        microserviceDashboardService.getProporcaoCustos(mes, ano, custoFixo, custoExtra),
        microserviceDashboardService.getTopServicos(mes, ano, 5),
        microserviceDashboardService.getTopProdutos(mes, ano, 5),
        microserviceDashboardService.getAtendimentosDiaSemana(mes, ano),
        microserviceDashboardService.getProporcaoAtendimentos(mes, ano),
        microserviceDashboardService.getReceitaPorUsuario(mes, ano),
        microserviceDashboardService.getOrdensPorUsuario(mes, ano),
        microserviceDashboardService.getComissaoProprietario(mes, ano, 30, custoFixo, custoExtra),
      ]);

      // Verificar se houve erros em alguma chamada
      const errors = [
        receitaBruta,
        receitaLiquida,
        margemLucro,
        numeroOrdens,
        ticketMedio,
        custoMedioServico,
        variacaoMensal,
        proporcaoCustos,
        topServicos,
        topProdutos,
        atendimentosDiaSemana,
        proporcaoAtendimentos,
        receitaPorUsuario,
        ordensPorUsuario,
        comissaoProprietario,
      ].filter((result) => !result.success);

      if (errors.length > 0) {
        return {
          success: false,
          error: `Erro ao carregar dados: ${errors.map((e) => e.error).join("; ")}`,
          partialData: {
            receitaBruta,
            receitaLiquida,
            margemLucro,
            numeroOrdens,
            ticketMedio,
            custoMedioServico,
            variacaoMensal,
            proporcaoCustos,
            topServicos,
            topProdutos,
            atendimentosDiaSemana,
            proporcaoAtendimentos,
            receitaPorUsuario,
            ordensPorUsuario,
            comissaoProprietario,
          },
        };
      }

      return {
        success: true,
        data: {
          receitaBruta,
          receitaLiquida,
          margemLucro,
          numeroOrdens,
          ticketMedio,
          custoMedioServico,
          variacaoMensal,
          proporcaoCustos,
          topServicos,
          topProdutos,
          atendimentosDiaSemana,
          proporcaoAtendimentos,
          receitaPorUsuario,
          ordensPorUsuario,
          comissaoProprietario,
        },
      };
    } catch (error) {
      return {
        success: false,
        error: `Erro ao carregar dashboard detalhada: ${error.message}`,
      };
    }
  },
};

export default microserviceDashboardService;
