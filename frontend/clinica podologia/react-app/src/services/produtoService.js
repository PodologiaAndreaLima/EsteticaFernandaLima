import servicoProdutoService from "./servicoProdutoService";

// Wrapper para produtos (que são ServicoProduto com isProduto = true)
export default {
  async list() {
    try {
      // Usar listAll() para pegar todos os itens, depois filtrar produtos
      const allItems = await servicoProdutoService.listAll();
      // Filtrar apenas produtos (isProduto = true ou is_produto = 1 ou produto = true)
      const produtos = Array.isArray(allItems) 
        ? allItems.filter(item => {
            const eProduto = item.isProduto === true || item.produto === true || item.is_produto === 1;
            return eProduto;
          })
        : [];
      console.log("DEBUG produtoService - Total itens:", allItems?.length, "Produtos filtrados:", produtos.length);
      console.log("DEBUG produtoService - Produtos encontrados:", produtos);
      return produtos;
    } catch (err) {
      console.error("produtoService.list error", err);
      return [];
    }
  },

  async listAll() {
    return this.list();
  },

  async getById(id) {
    try {
      return await servicoProdutoService.getById(id);
    } catch (err) {
      console.error("produtoService.getById error", err);
      throw err;
    }
  },

  async create(data) {
    // Garantir que isProduto seja true
    return await servicoProdutoService.create({ ...data, isProduto: true });
  },

  async update(id, data) {
    // Garantir que isProduto seja true
    return await servicoProdutoService.update(id, { ...data, isProduto: true });
  },

  async remove(id) {
    return await servicoProdutoService.remove(id);
  },

  async delete(id) {
    return await servicoProdutoService.remove(id);
  }
};
