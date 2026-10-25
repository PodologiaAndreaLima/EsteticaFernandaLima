# ordem-servico-ms

Microservico isolado de Ordem de Servico em Clean Architecture.

## Estrutura

- `domain`: regras e modelos de dominio
- `application`: casos de uso e DTOs
- `infrastructure`: persistencia (JPA)
- `adapters`: entrada HTTP (controller)

## Endpoints

- `POST /ordens-servico`
- `GET /ordens-servico/{id}`
- `GET /ordens-servico?page=0` (paginacao fixa de 10)
- `PUT /ordens-servico/{id}`
- `DELETE /ordens-servico/{id}`
- `PATCH /ordens-servico/{id}/status`

## Atendimento, status e retorno

POST e PUT recebem `dt_hora` no formato `2026-11-05T14:30:00`, sem conversão UTC,
e `retorno` booleano. `dt_hora` é obrigatório. O horário é o horário local do atendimento.
POST sempre inicia em `PENDENTE`; retorno omitido equivale a `false`.
Para retorno, a data é mantida e o horário inicial é normalizado para `00:00:00`.

Fluxo: `PENDENTE` → `EM_ANDAMENTO` → `CONCLUIDA`.
É possível cancelar a partir de `PENDENTE` ou `EM_ANDAMENTO`; estados finais não reabrem.
PATCH recebe `{"status":"EM_ANDAMENTO"}`. Opcionalmente pode receber `dt_hora`
para definir explicitamente o horário inicial de um retorno ao iniciar ou cancelar.
A data deve permanecer igual. Sem esse campo, o horário permanece inalterado.
PUT preserva `dataCriacao` e não permite mudar a identificação ou a data/hora de retorno.
Respostas de CRUD e PATCH incluem `dt_hora`, `status` e `retorno`.

## Migração

Flyway executa `V1__Atendimento_status_retorno` antes do Hibernate ao iniciar a versão atualizada.
O histórico usa `flyway_ordem_servico_history`, exclusivo deste microserviço.
Na tabela `ordem_servico_ms`, acrescenta `dt_hora DATETIME`, `status VARCHAR(16)` e `retorno BOOLEAN`.
Se `dt_hora` já existir como DATE, converte seu tipo preservando a data.
Registros sem esse campo recebem `data_criacao` à meia-noite, `PENDENTE` e `false`.
O nome usado pela entidade e pelo `data.sql` é `ordem_servico_ms`.
A V2 de renomeação é mantida no histórico para bancos que já a executaram;
`V3__Restaurar_nome_ordem_servico_ms` desfaz essa renomeação, preservando registros,
IDs, a coluna `data_criacao` e vínculos dos itens. Se as duas tabelas já existirem,
a migração interrompe a inicialização sem sobrescrever nenhuma delas.

Execute a nova versão do microserviço para aplicar a migração. Os testes usam H2 isolado;
não executam migrações no MySQL de desenvolvimento. O cliente web legado precisa enviar
o novo campo obrigatório ao criar/editar; não foi alterado por estar fora deste escopo.

## Dados de exemplo

O `src/main/resources/data.sql` carrega cinco ordens locais (IDs 1001 a 1005),
após a migração Flyway, cobrindo os quatro status e um retorno à meia-noite.
Registros com esses IDs são preservados, inclusive se tiverem sido editados.
Ordens de exemplo excluídas são recriadas na próxima inicialização com a carga ativa.
Os IDs de clientes 1, 2 e 3 e usuário 1 devem existir no backend principal;
ajuste as referências no script se seus cadastros usarem outros IDs.
A carga não cria itens de catálogo nem publica eventos RabbitMQ para dashboards.
Para desativá-la, configure `ORDENS_SEED_MODE=never` (recomendado em produção).

## Aplicativo mobile

`EXPO_PUBLIC_ORDERS_API_URL` aponta para este serviço (padrão `http://localhost:8081`).
`EXPO_PUBLIC_API_URL` aponta para os catálogos do backend principal (padrão `http://localhost:8080`).
Para o navegador local, `npm run web -- --port 3000` usa uma origem já permitida pelo serviço.
Em dispositivo físico, configure as URLs com o endereço de rede do computador.

No formulário existente, informe `DD/MM/AAAA HH:mm` para atendimento comum ou apenas
`DD/MM/AAAA` para retorno. O interruptor identifica a própria OS como retorno, sem criar
outra OS. Mantenha o cartão pressionado para editar; nos detalhes, toque no status para
iniciar, concluir ou cancelar. A troca do horário inicial de retorno exige escolha explícita.

A sessão segue o provedor `setOrdersTokenProvider`/sessionStorage já existente.
O login nativo ainda precisa fornecer token por esse provedor; não foi alterado neste módulo.
As listas relacionadas mantêm as permissões do backend principal, inclusive ADMIN em `/usuarios`.
As operações de escrita mantêm a publicação RabbitMQ existente e precisam de broker disponível.
Não foram adicionadas notificações, lembretes ou automações futuras.

## Executar

```bash
mvn spring-boot:run
```

## Testar

```bash
mvn test
```

