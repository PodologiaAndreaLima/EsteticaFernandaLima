/**
 * Modelo de Cliente que corresponde à entidade Cliente no backend Spring Boot
 */
class Cliente {
  constructor(data = {}) {
    this.id = data.id || null;
    this.nomeCompleto = data.nomeCompleto || "";
    this.cpf = data.cpf || "";
    this.telefone = data.telefone || "";
    this.email = data.email || "";
    this.senha = data.senha || "";
  }

  /**
   * Converte o objeto para envio à API
   * @returns {Object} Objeto pronto para ser enviado à API
   */
  toAPI() {
    // Cria um objeto com os campos exatamente como o backend espera
    const apiData = {
      nomeCompleto: this.nomeCompleto,
      cpf: this.cpf,
      telefone: this.telefone,
      email: this.email,
      senha: this.senha,
    };

    // Adiciona o ID apenas se existir
    if (this.id) {
      apiData.id = this.id;
    }

    return apiData;
  }

  /**
   * Cria uma instância a partir de dados da API
   * @param {Object} data Dados recebidos da API
   * @returns {Cliente} Nova instância de Cliente
   */
  static fromAPI(data) {
    return new Cliente({
      id: data.id,
      nomeCompleto: data.nomeCompleto,
      cpf: data.cpf,
      telefone: data.telefone,
      email: data.email,
      senha: data.senha,
    });
  }
}

export default Cliente;
