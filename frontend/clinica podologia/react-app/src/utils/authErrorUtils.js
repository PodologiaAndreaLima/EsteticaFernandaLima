const PASSWORD_POLICY_REQUIREMENTS = [
  "no minimo 8 caracteres",
  "uma letra maiuscula",
  "uma letra minuscula",
  "um numero",
  "um caractere especial",
];

const normalize = (text = "") =>
  String(text)
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();

export const extractApiErrorMessage = (error) => {
  const data = error?.response?.data;

  if (typeof data === "string") return data;
  if (typeof data?.message === "string") return data.message;
  if (Array.isArray(data?.message)) return data.message.join(" ");
  if (typeof data?.error === "string") return data.error;
  if (Array.isArray(data?.errors)) return data.errors.join(" ");

  return "";
};

export const validateStrongPassword = (password = "") => {
  const missing = [];

  if (password.length < 8) missing.push(PASSWORD_POLICY_REQUIREMENTS[0]);
  if (!/[A-Z]/.test(password)) missing.push(PASSWORD_POLICY_REQUIREMENTS[1]);
  if (!/[a-z]/.test(password)) missing.push(PASSWORD_POLICY_REQUIREMENTS[2]);
  if (!/[0-9]/.test(password)) missing.push(PASSWORD_POLICY_REQUIREMENTS[3]);
  if (!/[^A-Za-z0-9]/.test(password))
    missing.push(PASSWORD_POLICY_REQUIREMENTS[4]);

  return {
    isValid: missing.length === 0,
    missing,
  };
};

export const buildPasswordPolicyMessage = (missing = []) => {
  const items = missing.length ? missing : PASSWORD_POLICY_REQUIREMENTS;

  if (!missing.length) {
    return [
      "A senha deve seguir a politica de seguranca:",
      ...items.map((item) => `- ${item}`),
    ].join("\n");
  }

  return [
    "Senha fora da politica de seguranca.",
    "Ajuste os itens:",
    ...items.map((item) => `- ${item}`),
  ].join("\n");
};

const looksLikeBlockedAccount = (status, normalizedMessage) => {
  if (status === 423 || status === 429) return true;

  const hasBlockWord =
    normalizedMessage.includes("bloque") || normalizedMessage.includes("lock");
  const hasAttemptsWord =
    normalizedMessage.includes("tentativa") ||
    normalizedMessage.includes("attempt");
  const hasAccountWord =
    normalizedMessage.includes("conta") ||
    normalizedMessage.includes("account") ||
    normalizedMessage.includes("usuario") ||
    normalizedMessage.includes("user");

  // Some APIs only return short messages like "conta bloqueada".
  return (hasBlockWord && hasAttemptsWord) || (hasBlockWord && hasAccountWord);
};

const looksLikeInvalidCredentials = (status, normalizedMessage) => {
  if (status === 401) return true;

  return (
    normalizedMessage.includes("credenciais") ||
    normalizedMessage.includes("email ou senha") ||
    normalizedMessage.includes("usuario ou senha") ||
    normalizedMessage.includes("invalid credential")
  );
};

const looksLikePasswordPolicyError = (status, normalizedMessage) => {
  if (status !== 400 && status !== 422) return false;

  return (
    normalizedMessage.includes("senha") &&
    (normalizedMessage.includes("maiusc") ||
      normalizedMessage.includes("minusc") ||
      normalizedMessage.includes("especial") ||
      normalizedMessage.includes("numero") ||
      normalizedMessage.includes("8"))
  );
};

export const getFriendlyLoginError = (error) => {
  const status = error?.response?.status;
  const message = extractApiErrorMessage(error);
  const normalizedMessage = normalize(message);

  if (looksLikeBlockedAccount(status, normalizedMessage)) {
    return "Limite de tentativas atingido. Tente novamente mais tarde.";
  }

  if (looksLikeInvalidCredentials(status, normalizedMessage)) {
    return "Email ou senha incorretos.";
  }

  return message || "Erro ao realizar login";
};

export const getFriendlyPasswordError = (error, fallback) => {
  const status = error?.response?.status;
  const message = extractApiErrorMessage(error);
  const normalizedMessage = normalize(message);

  if (looksLikePasswordPolicyError(status, normalizedMessage)) {
    return buildPasswordPolicyMessage();
  }

  return message || fallback;
};
