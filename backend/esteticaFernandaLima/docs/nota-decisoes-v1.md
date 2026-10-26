# Contrato ETL v1 — decisões e implementação das rotas de nota

Autoridade: `.evercode/plans/etl-notas-estoque/05-phase1-contratos.md:10-49`; decisões de `02-analysis.md:17-34`, desenho `03-architecture.md:11-23`. DTO público: `main/java/lima/fernanda/esteticaFernandaLima/dto/nota/NotaContrato.java`; OpenAPI **estático e prospectivo**, `nota-contrato-v1.yaml` (não é a superfície publicada por SpringDoc). Nenhum campo interno/URL de provedor. Todo texto de nota/IA deve ser tratado como não confiável pela UI.

## Matriz de campos JSON (campos presentes mesmo quando nulos)

| Shape | Campo: tipo; nulidade | Regras |
|---|---|---|
| UploadResposta | notaId: int64 não nulo; status: EstadoNota não nulo | status sempre PROCESSANDO |
| NotaResposta | notaId: int64, status: EstadoNota não nulos; fornecedor: Fornecedor|null; dataEmissao: ISO date|null; totalNota: decimal|null; chaveAcesso: string|null; avisos: string[]; codigoFalha: CodigoFalha|null; itens: Item[]; resumoConfirmacao: ResumoConfirmacao|null | arrays nunca nulos; resumo somente CONFIRMADA; codigoFalha somente FALHA |
| Fornecedor | cnpj, razaoSocial: string|null | fornecedor inteiro também pode ser nulo |
| Item | itemId: int64; textoOriginal: string; quantidade: int|null; valorUnitario, valorTotal: decimal|null; produtoSugerido: ProdutoSugerido|null; confianca: ALTA/MEDIA/BAIXA|null; status: EstadoItem; motivoRejeicao: Rejeicao|null | motivo apenas INVALIDO/REJEITADO; sugestão não é aprovação |
| ProdutoSugerido | id: int64, nome: string não nulos | apenas próprio e elegível |
| Rejeicao | codigo: SEM_PRODUTO/REJEITADO_PELA_USUARIA/DADOS_INVALIDOS; texto: string até 160 | texto público curto, sem provider/raw |
| ConfirmarRequest | itens: DecisaoItem[] não nulo e não vazio | cada item da nota exatamente uma vez, IDs únicos; servidor verifica pertencimento |
| DecisaoItem | itemId: int64; acao: ASSOCIAR/CRIAR_NOVO/REJEITAR; produtoId: int64|null; criarNovo: boolean; nomeNovo: string|null; quantidade: int|null; valorUnitario: decimal|null | ASSOCIAR exige produto próprio, criarNovo=false, nomeNovo=null, quantidade/custo positivos; CRIAR_NOVO exige criarNovo=true, produtoId=null, nomeNovo não vazio/branco com no máximo 200 unidades de código UTF-16 (`String.length()`, medido antes do `trim()` aplicado ao persistir) e sem caracteres de controle ISO (`Character.isISOControl`: U+0000–U+001F e U+007F–U+009F; violação = 400 estrutural), quantidade/custo positivos, aceite individual; REJEITAR exige produtoId/nomeNovo/quantidade/valorUnitario=null e criarNovo=false |
| ResumoConfirmacao | notaId: int64, status: CONFIRMADA; itensConfirmados: ItemConfirmado[]; itensRejeitados: ItemRejeitado[]; produtosAfetados: ProdutoAfetado[] | arrays vazios válidos; mesmo shape em POST 200 e GET CONFIRMADA |
| ItemConfirmado | itemId, produtoId: int64; quantidade: int; custoUnitario: decimal | custo histórico não arredondado |
| ItemRejeitado | itemId: int64; motivo: MotivoRejeicao | motivo decidido pelo servidor |
| ProdutoAfetado | id: int64; estoqueAtual: int; ultimoCusto: decimal; valorVenda: decimal|null | somente da conta; último custo serializado por produto |
| Erro | codigo: CodigoErro; mensagem: string; notaId: int64|null; statusAtual: EstadoNota|null | notaId somente duplicata própria; statusAtual somente conflito próprio |
| ProdutoCatalogo | id: int64; nome: string | GET catálogo retorna array; só produto=true, ativo=true, usuario=principal |

IDs positivos; valores monetários como números JSON decimais exatos no servidor (BigDecimal), nunca conversão pt-BR via float. Nulos explícitos; desabilitar omissão de nulos na integração. Contrato rejeita propriedades adicionais e tipos inesperados na entrada; DTOs e anotações de validação são **forma**, não substituem validação contextual no serviço/controller. Escapar texto exibido da nota/IA; limitar tamanho de textos recebidos antes de persistir/exibir.

## Estados, transições, erros HTTP

| Operação/evento | Pré-estado | Pós-estado/HTTP | Falha pública |
|---|---|---|---|
| POST imagem única jpeg/png/webp, reserva + job duráveis | novo | PROCESSANDO, 202 + Location relativo + no-store | 400 imagem/partes/bytes/pixels, 409 duplicata própria, 413 opcional teto HTTP, 422 apenas ilegível síncrono, 429, 503 antes da reserva |
| Worker | PROCESSANDO | EM_REVISAO ou FALHA (GET 200) | FALHA com IMAGEM_ILEGIVEL/EXTRACAO_INVALIDA/NOTA_INVALIDA/PROVEDOR_INDISPONIVEL/DUPLICADA/PROCESSAMENTO_FALHOU |
| POST descartar | PROCESSANDO ou EM_REVISAO | DESCARTADA, 204 sem body | 409 terminal; job não sobrescreve descarte |
| POST confirmar | EM_REVISAO | CONFIRMADA, 200 com resumo persistido | 400 lista/linha estrutural, 404 alheia/inexistente, 409 concorrente ou status incompatível |
| GET {id} | qualquer estado | 200, no-store | 404 alheia/inexistente |
| GET catálogo ETL | conta autenticada | 200 array filtrado, no-store | nunca usar GET legado sem owner |

Todos exigem JWT Bearer, dono checado em **cada** acesso e rate limit de leitura/upload/confirmar/catálogo/descarte; 401/403/429 pertinentes, 500/503 sempre sanitizados. Respostas JSON `application/json; charset=utf-8` exceto 204. Item PENDENTE→VALIDO/INVALIDO; VALIDO→CONFIRMADO/REJEITADO; INVALIDO→REJEITADO salvo correção explícita validada que primeiro o torna válido; PENDENTE nunca confirma. PROCESSANDO não tem itens revisáveis; EM_REVISAO mostra todos; CONFIRMADA só CONFIRMADO/REJEITADO e contagens coerentes; DESCARTADA nunca gera movimento e retém status anterior dos itens. Confirmar todos REJEITAR é permitido **com ciência explícita da usuária**: CONFIRMADA, itensConfirmados/produtosAfetados vazios, nenhum movimento. Timeout de POST confirmar: GET da mesma nota recupera resumoConfirmacao; nunca repetir POST automaticamente. Repetição retorna 409 com estado próprio.

## Decisões fechadas D2/D3/D4/D6/D7/Q1–Q3

- **D2**: reserva UNIQUE física por `(usuario_id,tipo_identidade,identidade_digest)`; chave de acesso normalizada prevalece mesmo com foto diferente; sem chave, digest canônico de CNPJ/data/total **e** SHA-256 de imagem; metadados insuficientes usam somente hash com namespace próprio. Identidade só pode ser liberada após descarte atômico, sem ressuscitar nota antiga; novo upload após FALHA também é nova nota sujeita à dedupe. Recompactação pode alterar hash; não alegar detecção perfeita. Colisão tardia vira FALHA/DUPLICADA, sem id alheio.
- **D3 (aprovada)**: apagar imagem ao alcançar estado final CONFIRMADA/FALHA/DESCARTADA; enquanto PROCESSANDO/EM_REVISAO acesso privado mínimo, fora de webroot, armazenamento protegido em repouso. Hash e dados estruturados de nota/itens/movimentos persistem auditáveis; **nunca persistir JSON bruto** (a menção ao JSON bruto em `03-architecture.md:14,19` foi substituída por esta decisão). Exclusão ocorre de forma segura e falhas são registradas com código/status/instante para retry operacional limitado, sem path/imagem/JSON/identificadores pessoais em logs públicos; transição não apaga estoque/histórico, confirmar nunca depende de arquivo já excluído. Implementação/TTL de retry a definir na fase de persistência, não afirmar expurgo já implementado.
- **D4**: principal autenticado é dona apenas dos próprios produtos, sem inferência de role inexistente; verificar owner e ativo/produto=true no servidor para catálogo, sugestão, associação e criação, com aceite explícito por item e confirmação geral. UI não é barreira de autorização.
- **D6**: quantidade inteira positiva, sem conversão de unidades. Valor unitário decimal de 0.01 a 9999999.99 com até 2 casas na entrada (regra para `despesa` Float legada; valores históricos permanecem BigDecimal). Verificar limite/precisão antes de `floatValue()`; não atualizar estoque via PUT legado. Tolerância de divergência de total 0.02 decimal gera aviso, jamais arredondamento silencioso. Transação única/lock por produto, estoque sem atualização perdida, movimento único por item, último custo da confirmação serializada (não data fiscal); rejeição parcial por dados inválidos não bloqueia outros, falha transacional reverte tudo.
- **D7**: job/lease persistidos no mesmo banco na reserva; worker bounded, retoma PROCESSANDO após restart e lease vencido, no máximo 3 tentativas, lease 60s > timeout Gemini 30s; CAS impede job de salvar após DESCARTADA. Nenhum retry automático de confirmação.
- **Q1**: `NotaProperties` e `application.properties` existentes definem imagem até 5.242.880 bytes / 12.000.000 pixels, worker 2, Gemini timeout 30s, lease 60s, 3 tentativas, tolerância 0.02, rate 30 requisições/minuto e 5 uploads/minuto por principal; app deve expor limites de imagem. Polling recomendado a cada **3 segundos** apenas enquanto PROCESSANDO; cancelar em terminal/unmount, sem retry automático de POST; a cadência do app ainda não é implementada, não há propriedade de polling no backend.
- **Q2**: produto histórico sem proprietário não participa de catálogo/matching/confirmação, saneamento administrativo separado; data fiscal inválida/ausente é FALHA/NOTA_INVALIDA, pedir nova foto (sem adivinhar data).
- **Q3**: consulta de duplicata sempre restrita ao dono, `notaId` só da conta; GET alheio responde 404; 409 de confirmação repetida expõe apenas `statusAtual` próprio. Resolver ACK incerto com GET.

## Exemplos sintéticos para o app

`POST /estoque/notas` → `202 {"notaId":42,"status":"PROCESSANDO"}`. `GET /estoque/notas/42` → `200 {"notaId":42,"status":"FALHA","fornecedor":null,"dataEmissao":null,"totalNota":null,"chaveAcesso":null,"avisos":[],"codigoFalha":"NOTA_INVALIDA","itens":[],"resumoConfirmacao":null}`. `POST /estoque/notas/42/confirmar` (após confirmação anterior) → `409 {"codigo":"CONFLITO_ESTADO","mensagem":"Confira o estado da nota.","notaId":null,"statusAtual":"CONFIRMADA"}`. `GET` CONFIRMADA inclui `resumoConfirmacao` com as cinco chaves de `ResumoConfirmacao`, mesmo se os três arrays estiverem vazios; `GET /estoque/notas/produtos` → `200 [{"id":17,"nome":"Produto cadastrado"}]`.

**Gates pendentes de integração**: endpoint catálogo adicionado ao contrato por necessidade do app, não constava nos quatro endpoints de 05; backend/app devem ratificar antes de implementar. Anotações DTO não impõem sozinhas correspondência de IDs, combinações condicionais, limites em respostas, no-store, ownership, rate limit, sanitização, SHA-256, exclusão ou transições; testar na fase de lógica. Catálogo não pode reutilizar `ServicoProdutoController` legado (`controller/ServicoProdutoController.java:28-38`) pois devolve entidade sem owner; segredo legado/configuração global e TRACE em `SecurityConfiguracao.java:112-132` são achados preexistentes fora deste escopo.
