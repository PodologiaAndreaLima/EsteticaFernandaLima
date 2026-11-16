# Módulo de mensagens e lembretes

O módulo foi adicionado ao backend Spring Boot existente, sem alterar clientes,
ordens de serviço ou autenticação. Como ainda não existe uma entidade de
agendamento no projeto, `agendamentoId` é opcional e pode ser preenchido quando
esse domínio for implementado.

## Endpoints

| Método | Rota | Função |
|---|---|---|
| POST | `/lembretes` | Cria um lembrete |
| GET | `/lembretes?page=0&size=20&clinicaId=default` | Lista lembretes paginados |
| GET | `/lembretes/{id}?clinicaId=default` | Consulta um lembrete |
| PATCH | `/lembretes/{id}/cancelar?clinicaId=default` | Cancela um lembrete pendente |
| POST | `/lembretes/{id}/gerar?clinicaId=default` | Gera sugestão para revisão, sem enviar |
| POST | `/lembretes/{id}/enviar?canal=WHATSAPP&clinicaId=default` | Registra o envio do conteúdo revisado e devolve o `whatsappUrl` |
| GET | `/mensagens?page=0&size=20&lembreteId=1&clinicaId=default` | Consulta histórico paginado |
| GET | `/lembretes/historico/mensagens?page=0&size=20` | Alias do histórico |

O Swagger fica disponível em `/swagger-ui/index.html`.

## Regras

- O cliente precisa existir e possuir telefone para envio.
- Lembretes são criados como `PENDENTE`, passam a `ENVIADO` quando o envio é
  registrado e podem ser cancelados apenas enquanto pendentes.
- Lembrete cancelado não pode ser enviado. Lembrete já enviado pode ser
  reenviado, porque o envio é manual (cada tentativa fica no histórico).
- Lembrete de agendamento precisa ter `dataHora` no futuro; lembrete de
  `RETORNO` aceita a data do último atendimento (passada).
- A geração retorna uma sugestão e não cria histórico nem chama o provedor de envio.
- O envio pode receber o texto revisado em `EnviarMensagemRequest.conteudo`.
- Falhas do provedor ficam no histórico como `FALHA`, com a mensagem do erro.
- A combinação clínica, cliente, data/hora e tipo não pode ser duplicada.
- `clinicaId` usa `default` quando omitido; todas as consultas de lembretes e
  mensagens filtram pelo tenant para impedir vazamento entre clínicas.
- Os estados possíveis são `PENDENTE`, `ENVIADO`, `CANCELADO`,
  `GERADA`, `ENVIADA` e `FALHA`.

## Lembretes a partir das ordens de serviço

Ao listar (`GET /lembretes`), o backend consulta o microsserviço de OS
(`ordemservico.ms.base-url`) e monta os lembretes automaticamente:

- **Horário:** OS que não é retorno, com status `PENDENTE` e `dt_hora` no futuro.
- **Retorno:** OS marcada como `retorno` e status `PENDENTE`. A data do retorno é o
  `dt_hora` dela; o procedimento e a data do último atendimento vêm da última OS
  `CONCLUIDA` da mesma cliente antes do retorno.
- O nome do procedimento vem dos itens da OS (serviço/produto ou combo cadastrados).
- Quando a OS é concluída, cancelada, excluída ou o horário passa, o lembrete
  pendente vira `CANCELADO` (some da lista). Lembretes já enviados não mudam.
- O id da OS fica em `agendamentoId`. Lembretes criados manualmente continuam valendo.
- Se o microsserviço estiver fora do ar, a lista mostra o que já existe.

## Envio pelo WhatsApp (manual)

O provedor padrão é `whatsapp-link` (`MESSAGENS_PROVIDER`). O backend não
dispara a mensagem: `POST /lembretes/{id}/enviar` grava o texto revisado no
histórico e devolve `whatsappUrl` no formato
`https://wa.me/55DDDNUMERO?text=MENSAGEM`. O app abre esse link e o WhatsApp do
celular já aparece na conversa do cliente com a mensagem digitada; o usuário
só toca em enviar. O telefone do cliente é normalizado (só dígitos, com DDI 55
quando vier apenas DDD + número). Telefone inválido gera registro `FALHA`.

`GET /lembretes` também devolve `telefoneCliente` para o app exibir no modal.

Outros provedores: `fake` (simulação para testes) e `http` (gateway real).

## Execução

Na pasta `backend/esteticaFernandaLima`:

```powershell
.\mvnw.cmd spring-boot:run
```

As tabelas podem ser criadas pelo `ddl-auto=update` existente ou pela migration
`src/main/resources/db/migration/V1__create_messaging_tables.sql` em ambientes
que utilizem Flyway. A migration já inclui os dados de retorno e procedimento usados
na geração de `RETORNO`. O seed opcional está em
`src/main/resources/data-messaging.sql` e não é executado automaticamente.

## Teste local da OpenAI

A integração vem desligada por padrão (sem a chave, o backend usa o gerador
padrão de texto e sobe normalmente). Ela é habilitada somente com
`MESSAGENS_AI_ENABLED=true` e lê a chave
exclusivamente de `OPENAI_API_KEY`. A chave não deve ser colocada no código,
em arquivos versionados, no Git ou em logs.

No PowerShell, antes de iniciar o backend:

```powershell
$env:OPENAI_API_KEY = "cole-a-chave-apenas-no-terminal-local"
$env:MESSAGENS_AI_ENABLED = "true"
.\mvnw.cmd spring-boot:run
```

No Git Bash:

```bash
export OPENAI_API_KEY='cole-a-chave-apenas-no-terminal-local'
export MESSAGENS_AI_ENABLED=true
./mvnw spring-boot:run
```

Também é possível configurar essas variáveis na configuração de execução do
IntelliJ IDEA, sem salvá-las no projeto. Autentique-se com um JWT válido, use
um `id` retornado por `GET /lembretes?clinicaId=default` e chame
`POST /lembretes/{id}/gerar?clinicaId=default`. O retorno contém apenas a
sugestão para revisão; não chame `/enviar` durante esse teste.

### Como o prompt é montado

- **Instruções fixas (system):** tom cordial, linguagem neutra quanto a gênero,
  no máximo 3 frases (até 300 caracteres), no máximo 1 emoji, nome da clínica
  uma vez, nada inventado, texto variado a cada geração, regra por tipo (`LEMBRETE_AGENDAMENTO` termina
  dizendo que basta responder para remarcar; `RETORNO` convida a responder para
  agendar) e um exemplo fictício de cada tipo.
- **Dados (user):** tipo, nome da clínica, primeiro nome, procedimento e datas
  já formatadas em português (ex.: `segunda-feira, 26/10/2026 às 10:00`), para
  a IA só copiar, e uma "variação de estilo" sorteada a cada pedido (para o
  "Gerar outra mensagem" trazer um texto diferente). Telefone e sobrenome não são enviados.

Variáveis opcionais:

| Variável | Padrão | Uso |
|---|---|---|
| `OPENAI_TEMPERATURE` | `0.9` | 0 = mais previsível, 2 = mais criativo |
| `CLINICA_NOME` | `Fernanda Lima Beauty Care` | Nome que aparece na mensagem |

Para simular falha do provedor fake:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--mensagens.fake-provider.fail=true"
```

## Testes

```powershell
.\mvnw.cmd test
```

Os testes do módulo cobrem deduplicação, tentativa sem telefone, validação
HTTP, reenvio, datas por tipo de lembrete e a montagem do link do WhatsApp.

Senha do banco: use as variáveis `DB_USER` e `DB_PASSWORD` (na configuração de
execução do IntelliJ), em vez de escrever a senha no `application.properties`.
No estado atual do repositório, a suíte completa também contém testes legados
que não compilam por APIs ausentes de `ComboService` e
`ServicoProdutoService`; isso é independente do módulo de mensagens.
