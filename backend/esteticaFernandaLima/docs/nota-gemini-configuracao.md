# Gemini no ETL de nota fiscal — configuração

O backend usa o Google Gemini para ler a foto da nota fiscal enviada pelo app
(`POST /estoque/notas`) e extrair fornecedor, data, total, chave de acesso e
itens.

- Dependência: `com.google.genai:google-genai` (SDK oficial) no `pom.xml`.
- Cliente: `integration/gemini/GeminiNotaClient.java` (prompt fixo, resposta em
  JSON pedido no prompt e validado por `RespostaNotaParser`, temperatura 0,
  timeout `nota.gemini.timeout`).
- O cliente só é criado quando `GEMINI_API_KEY` existe. Sem a chave o backend
  sobe normalmente e o upload de nota responde **503 SERVICO_INDISPONIVEL**.

A chave fica **só em variável de ambiente**. Não coloque em
`application.properties`, código, Git ou logs, e nunca no app mobile.

## Foto da nota

A foto **não é gravada em disco nem no banco**. Ela fica só na memória do
processo (`ArmazenamentoImagemNotaMemoria`) até o Gemini responder; depois é
removida. Consequências:

- Não há pasta privada para configurar e o upload funciona em Windows, Linux e
  macOS.
- Se o backend reiniciar enquanto a nota está `PROCESSANDO`, ela vira `FALHA`
  com `PROCESSAMENTO_FALHOU`. A usuária envia a foto de novo.
- O backend precisa rodar em **uma única instância**, porque a foto fica na
  memória do processo que recebeu o upload.

## Variáveis

| Variável | Obrigatória | Padrão | Uso |
|---|---|---|---|
| `GEMINI_API_KEY` | para o ETL funcionar | — | Chave do Google AI Studio |
| `GEMINI_MODEL` | não | `gemini-3.5-flash-lite` | Troca o modelo sem recompilar (ex.: `gemini-3.8-flash`) |
| `JWT_SECRET` | sim | — | Já existia |
| `DB_PASSWORD` | sim (no seu ambiente) | — | Já existia |

## 1. Gerar a chave

1. Acesse <https://aistudio.google.com/apikey> com a conta Google da clínica.
2. **Create API key** → escolha (ou crie) um projeto do Google Cloud.
3. Copie a chave e guarde num gerenciador de senhas. Se vazar, apague-a no
   AI Studio e gere outra.

O plano gratuito tem limite de requisições e o Google pode usar os dados
enviados para melhorar os modelos. Como a foto da nota tem dados fiscais, ative
o faturamento no projeto antes de usar com notas reais.

## 2. Rodar pelo IntelliJ

*Run → Edit Configurations… → (configuração do Spring Boot) → Environment
variables*, adicione:

```
GEMINI_API_KEY=sua-chave;JWT_SECRET=...;DB_PASSWORD=...
```

Essas variáveis ficam na pasta `.idea/` (que está no `.gitignore`), não no
repositório.

## Como verificar

1. Logado no app, envie uma foto de nota.
2. `POST /estoque/notas` deve responder **202**. 503 indica chave ausente.
3. `GET /estoque/notas/{id}` passa de `PROCESSANDO` para `EM_REVISAO`.
   `FALHA` com `PROVEDOR_INDISPONIVEL` costuma ser chave inválida, modelo
   inexistente para a sua chave, cota estourada ou timeout.

## Escolha do modelo

Em out/2026 o Google restringiu os modelos 2.5 a contas que já os usavam e
recomenda `gemini-3.5-flash-lite` ou `gemini-3.8-flash` para projetos novos.
O padrão é o Flash-Lite (mais barato e rápido); se a extração errar muito,
teste `GEMINI_MODEL=gemini-3.8-flash`. Lista atual:
<https://ai.google.dev/gemini-api/docs/models>.
