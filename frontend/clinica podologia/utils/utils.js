// Formatar Data no padrão BR

// O que faz:

// Cria um objeto Date com a data recebida.

// Usa o método toLocaleDateString("pt-BR"), que formata no padrão brasileiro (dd/mm/aaaa).

// Exemplo: "2025-08-26" → "26/08/2025".
export function formatarDataBR(data) {
  const d = new Date(data);
  return d.toLocaleDateString("pt-BR"); // Ex: 26/08/2025
}

// Formatar Hora no padrão BR
// O que faz:

// Cria um Date com a data/hora recebida.

// Usa toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" }) para formatar somente horas e minutos.

// Exemplo: "2025-08-26T17:30:00" → "17:30".
export function formatarHoraBR(data) {
  const d = new Date(data);
  return d.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" });
}

// Validar Telefone BR
export function validarTelefoneBR(telefone) {
  if (!telefone) return false;

  // 1. Remover tudo que não for número
  let apenasNumeros = "";
  for (let i = 0; i < telefone.length; i++) {
    const char = telefone[i];
    if (char >= "0" && char <= "9") {
      apenasNumeros += char;
    }
  }

  // 2. O telefone brasileiro deve ter entre 10 e 11 dígitos:
  //    (2 do DDD + 8 ou 9 do número)
  if (apenasNumeros.length < 10 || apenasNumeros.length > 11) {
    return false;
  }

  // 3. Validar DDD (simplificado: deve ser entre 11 e 99)
  const ddd = parseInt(apenasNumeros.substring(0, 2));
  if (ddd < 11 || ddd > 99) {
    return false;
  }

  // 4. Validar número
  const numero = apenasNumeros.substring(2);

  // se tiver 8 dígitos → telefone fixo
  if (numero.length === 8) {
    return true;
  }

  // se tiver 9 dígitos → celular (tem que começar com 9)
  if (numero.length === 9 && numero[0] === "9") {
    return true;
  }

  return false;
}

// Validar email

export function isEmailValido(email) {
  if (!email) return false;

  // precisa ter um "@"
  const atIndex = email.indexOf("@");
  if (atIndex === -1) return false;

  // não pode começar ou terminar com "@"
  if (atIndex === 0 || atIndex === email.length - 1) return false;

  // pega a parte depois do "@"
  const dominio = email.slice(atIndex + 1);

  // domínio precisa ter "."
  const dotIndex = dominio.indexOf(".");
  if (dotIndex === -1) return false;

  // "." não pode ser o último caractere
  if (dotIndex === dominio.length - 1) return false;

  // garante que não tem espaço em branco
  if (email.includes(" ")) return false;

  return true;
}

// Validar CPF
export function validarCPF(cpf) {
  if (!cpf) return false;

  // 1. Remover tudo que não for número
  let apenasNumeros = "";
  for (let i = 0; i < cpf.length; i++) {
    const char = cpf[i];
    if (char >= "0" && char <= "9") {
      apenasNumeros += char;
    }
  }
  cpf = apenasNumeros;

  // 2. Verificar tamanho
  if (cpf.length !== 11) return false;

  // 3. Bloquear CPFs com todos os dígitos iguais
  let todosIguais = true;
  for (let i = 1; i < cpf.length; i++) {
    if (cpf[i] !== cpf[0]) {
      todosIguais = false;
      break;
    }
  }
  if (todosIguais) return false;

  // 4. Calcular 1º dígito verificador
  let soma = 0;
  for (let i = 0; i < 9; i++) {
    soma += parseInt(cpf[i]) * (10 - i);
  }
  let resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf[9])) return false;

  // 5. Calcular 2º dígito verificador
  soma = 0;
  for (let i = 0; i < 10; i++) {
    soma += parseInt(cpf[i]) * (11 - i);
  }
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf[10])) return false;

  return true;
}
