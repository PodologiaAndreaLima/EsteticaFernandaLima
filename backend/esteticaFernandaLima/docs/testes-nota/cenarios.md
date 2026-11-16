# Cenários de teste — ETL de nota fiscal

Estudo do fluxo para cobrir os cenários principais. Todas as notas são **fictícias** (fornecedor de exemplo, sem valor fiscal).

## Fluxo resumido

```
upload (POST /estoque/notas)
  ├─ 400 IMAGEM_INVALIDA      formato/tamanho/pixels inválidos
  ├─ 409 DUPLICADA            mesma imagem já enviada pelo usuário
  └─ 202 PROCESSANDO
        └─ worker chama o Gemini
             ├─ FALHA PROVEDOR_INDISPONIVEL  erro/timeout do Gemini (3 tentativas)
             ├─ FALHA EXTRACAO_INVALIDA      resposta fora do formato
             ├─ FALHA IMAGEM_ILEGIVEL        legivel = false
             ├─ FALHA NOTA_INVALIDA          normalizador recusou (data, total, itens)
             ├─ FALHA DUPLICADA              chave de acesso já usada pelo usuário
             └─ EM_REVISAO                   avisos podem existir (CNPJ_INVALIDO, TOTAL_DIVERGENTE, POSSIVEL_DUPLICATA, CHAVE_NAO_CONFIRMADA)
                   ├─ descartar  → DESCARTADA (204)
                   └─ confirmar  → CONFIRMADA (200) + movimentação de estoque
                                   (repetir → 409 CONFLITO_ESTADO)
```

## Regras que os cenários exercitam

| Regra | Onde está |
|---|---|
| Data `AAAA-MM-DD`, não futura | `NormalizadorExtracaoNota.data` |
| Total positivo, até 9.999.999,99 | `NormalizadorExtracaoNota.validar` |
| Chave de acesso com 44 dígitos e DV módulo 11; se inválida, a nota segue sem chave com aviso `CHAVE_NAO_CONFIRMADA` | `NormalizadorExtracaoNota.chaveValida` |
| CNPJ com DV válido (só gera aviso) | `NormalizadorExtracaoNota.cnpjValido` |
| Item `INVALIDO` quando `qtd × unitário ≠ total` (tolerância 0,02) | `NormalizadorExtracaoNota.linha` |
| Nota sem nenhum item válido → `NOTA_INVALIDA` | `NormalizadorExtracaoNota.validar` |
| Soma dos itens ≠ total → aviso `TOTAL_DIVERGENTE` | `NormalizadorExtracaoNota.validar` |
| Dedupe por imagem (SHA-256) no upload | `SubmissaoNotaService` |
| Dedupe por chave de acesso | `ProcessadorExtracaoNota.publicar` |
| Possível duplicata sem chave (CNPJ + data + total) → aviso | `ProcessadorExtracaoNota.publicar` |
| Confirmação só em `EM_REVISAO`; item `INVALIDO` precisa ser decidido | `NotaFiscalConfirmacaoService` |
| Descartar em `PROCESSANDO` ou `EM_REVISAO` | `NotaFiscalConfirmacaoService`, `NotaFiscalRevisaoService` |
| Limite de 5 uploads por minuto | `NotaFiscalProtecaoFilter` |

## Matriz de cenários

Os arquivos são gerados por [gerar-imagens.ps1](gerar-imagens.ps1) e executados por [rodar-cenarios.mjs](rodar-cenarios.mjs). A verificação no banco está em [verificacao.sql](verificacao.sql).

| Caso | Entrada | Resultado esperado | Verificar nas tabelas |
|---|---|---|---|
| C01 completa com chave | `c01` | `EM_REVISAO`, sem avisos; confirmada cria produtos | `nota_fiscal_identidade` (CHAVE_ACESSO); `movimentacao_estoque` |
| C02 completa sem chave | `c02` | `EM_REVISAO`, sem avisos; confirmada cria produtos | `nota_fiscal_identidade` (SOMENTE_IMAGEM) |
| C03 mesma chave, outra foto | `c03` | `FALHA DUPLICADA` | nota sem itens persistidos; sem nova identidade de chave |
| C04 mesma nota sem chave, outra foto | `c04` | `EM_REVISAO` com `POSSIVEL_DUPLICATA` | aviso em `avisos_extracao` |
| C05 mesma imagem de C02 | cópia byte a byte | `409 DUPLICADA` no upload, com `notaId` de C02 | nenhuma nota nova |
| C06 chave com DV errado | `c06` | `EM_REVISAO` com `CHAVE_NAO_CONFIRMADA` | `chave_acesso` vazia; identidade SOMENTE_IMAGEM |
| C07 sem total | `c07` | `FALHA NOTA_INVALIDA` | — |
| C08 sem data | `c08` | `FALHA NOTA_INVALIDA` | — |
| C09 data futura | `c09` | `FALHA NOTA_INVALIDA` | — |
| C10 imagem ilegível | `c10` | `FALHA` (IMAGEM_ILEGIVEL ou outro, conforme o Gemini) | código real registrado |
| C11 um item inconsistente | `c11` | `EM_REVISAO`, item 1 `INVALIDO`; confirmar sem decidir o inválido → 400; confirmar rejeitando-o → `CONFIRMADA` | `nota_fiscal_item.status`; movimento só do item válido |
| C12 total divergente | `c12` | `EM_REVISAO` com `TOTAL_DIVERGENTE` | aviso |
| C12b todos os itens inconsistentes | `c12b` | `FALHA NOTA_INVALIDA` | — |
| C13 CNPJ inválido | `c13` | `EM_REVISAO` com `CNPJ_INVALIDO`; `fornecedor_cnpj` vazio | aviso |
| C14 arquivo não é imagem | `.txt` | `400 IMAGEM_INVALIDA` | — |
| C15 imagem maior que 5 MB | PNG grande | 400/413 | — |
| C17 descartar em `EM_REVISAO` | `c17` | `DESCARTADA` (204); confirmar depois → `409` | `nota_fiscal_identidade` liberada |
| C18 rejeitar todos os itens | `c18` | `CONFIRMADA` sem movimento | nenhuma linha em `movimentacao_estoque` |
| C19 item sem quantidade | `c19` | `EM_REVISAO` com item `INVALIDO` | — |
| C20 foto que não é nota | `c20` | `FALHA` | — |
| Consulta inexistente | id 999999 | `404` | — |
| Consulta sem token | — | `401` | — |
| Rajada de uploads | 6 envios seguidos | 429 no sexto | — |

## Cenários que não entram na automação

- **Reinício do backend durante `PROCESSANDO`:** a nota deve virar `FALHA PROCESSAMENTO_FALHOU` (ou ser retomada, conforme o lease). Precisa de teste manual: enviar uma foto e reiniciar o backend logo em seguida.
- **Gemini indisponível (`PROVEDOR_INDISPONIVEL`):** exige chave inválida ou corte de rede. Não rodar com a chave de produção.
- **Confirmar item de outra usuária (`ASSOCIAR` com produto alheio):** precisa de segundo usuário.
- **Tela do app:** revisão e confirmação na interface não estão cobertas pela API.
