/**
 * Modelo para representar um Funcionário no sistema
 * Esta classe é responsável por converter os dados entre o formato do frontend e o formato da API
 */
class Funcionario {
  constructor(data = {}) {
    this.id = data.id || null;
    this.nomeCompleto = data.nomeCompleto || "";
    this.cpf = data.cpf || "";
    this.telefone = data.telefone || "";
    this.email = data.email || "";
    this.cargo = data.cargo || "";
    this.isStaff = true; // Sempre true para funcionários
    this.isAtivo = data.isAtivo !== undefined ? data.isAtivo : true;
    this.dataCadastro = data.dataCadastro || null;
  }

  // Converte do formato do frontend para o formato da API
  toAPI() {
    return {
      id: this.id,
      nomeCompleto: this.nomeCompleto,
      cpf: this.cpf,
      telefone: this.telefone,
      email: this.email,
      cargo: this.cargo,
      isStaff: this.isStaff,
      isAtivo: this.isAtivo,
      dataCadastro: this.dataCadastro,
    };
  }

  // Converte do formato da API para o formato do frontend
  static fromAPI(data) {
    if (!data) return null;

    return new Funcionario({
      id: data.id,
      nomeCompleto: data.nomeCompleto,
      cpf: data.cpf,
      telefone: data.telefone,
      email: data.email,
      cargo: data.cargo,
      isStaff: true,
      isAtivo: data.isAtivo,
      dataCadastro: data.dataCadastro,
    });
  }
}

export default Funcionario;
