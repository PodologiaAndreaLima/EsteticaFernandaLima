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
| POST | `/lembretes/{id}/enviar?canal=WHATSAPP&clinicaId=default` | Envia conteúdo revisado |
| GET | `/mensagens?page=0&size=20&lembreteId=1&clinicaId=default` | Consulta histórico paginado |
| GET | `/lembretes/historico/mensagens?page=0&size=20` | Alias do histórico |

O Swagger fica disponível em `/swagger-ui/index.html`.

## Regras

- O cliente precisa existir e possuir telefone para envio.
- Lembretes são criados como `PENDENTE`, passam a `ENVIADO` somente após o
  provedor aceitar a mensagem e podem ser cancelados apenas enquanto pendentes.
- Lembrete cancelado não pode ser enviado.
- A geração retorna uma sugestão e não cria histórico nem chama o provedor de envio.
- O envio pode receber o texto revisado em `EnviarMensagemRequest.conteudo`.
- Falhas do provedor ficam no histórico como `FALHA`, com a mensagem do erro.
- A combinação clínica, cliente, data/hora e tipo não pode ser duplicada.
- `clinicaId` usa `default` quando omitido; todas as consultas de lembretes e
  mensagens filtram pelo tenant para impedir vazamento entre clínicas.
- Os estados possíveis são `PENDENTE`, `ENVIADO`, `CANCELADO`,
  `GERADA`, `ENVIADA` e `FALHA`.

## Execução

Na pasta `backend/esteticaFernandaLima`:

```powershell
.\mvnw.cmd spring-boot:run
```

As tabelas podem ser criadas pelo `ddl-auto=update` existente ou pela migration
`src/main/resources/db/migration/V1__create_messaging_tables.sql` em ambientes
que utilizem Flyway. A migration inclui os dados de retorno e procedimento usados
na geração de `RETORNO`. O seed opcional está em
`src/main/resources/data-messaging.sql` e não é executado automaticamente.

## Teste local da OpenAI

A integração é habilitada somente com `MESSAGENS_AI_ENABLED=true` e lê a chave
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

Para simular falha do provedor fake:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--mensagens.fake-provider.fail=true"
```

## Testes

```powershell
.\mvnw.cmd test
```

Os testes novos cobrem deduplicação, tentativa sem telefone e validação HTTP.
No estado atual do repositório, a suíte completa também contém testes legados
que não compilam por APIs ausentes de `ComboService` e
`ServicoProdutoService`; isso é independente do módulo de mensagens.
