-- Verificação dos cenários de teste do ETL de nota fiscal (banco esteticafernandalima).
-- Rode depois de executar: node rodar-cenarios.mjs <pasta> <email> <senha>

-- 1) Visão geral de todas as notas e o estado final de cada uma
SELECT id, status, codigo_falha, fornecedor_cnpj, fornecedor_nome, data_emissao, total_nota,
       chave_acesso, avisos_extracao, estado_limpeza, finalizada_em
FROM nota_fiscal_importacao
ORDER BY id;

-- 2) Itens de cada nota (status do item e produto associado)
SELECT i.nota_id, i.ordem, i.texto_original, i.quantidade, i.valor_unitario, i.valor_total,
       i.status, i.produto_sugerido_id, i.motivo_rejeicao, i.confianca
FROM nota_fiscal_item i
ORDER BY i.nota_id, i.ordem;

-- 3) Identidades reservadas para deduplicação (uma linha por nota que chegou a publicar chave ou imagem)
SELECT nota_id, tipo_identidade, identidade_digest
FROM nota_fiscal_identidade
ORDER BY nota_id;

-- 4) Movimentações de estoque geradas pelas confirmações (uma linha por item confirmado)
SELECT m.id, m.item_id, i.nota_id, m.produto_id, m.quantidade, m.custo_unitario,
       m.estoque_apos, m.valor_venda_apos, m.registrada_em
FROM movimentacao_estoque m
JOIN nota_fiscal_item i ON i.id = m.item_id
ORDER BY m.id;

-- 5) Produtos criados ou atualizados pelo ETL, com o estoque atual
SELECT id_produto_servico, nome, estoque, despesa, valor_venda
FROM servico_produto
WHERE id_produto_servico IN (SELECT produto_id FROM movimentacao_estoque)
ORDER BY id_produto_servico;

-- 6) Jobs de processamento: tentativas e estado de cada nota
SELECT nota_id, status, tentativas, lease_ate, versao
FROM nota_fiscal_job
ORDER BY nota_id;

-- 7) Resumo: contagem de notas por estado final
SELECT status, codigo_falha, COUNT(*) AS quantidade
FROM nota_fiscal_importacao
GROUP BY status, codigo_falha
ORDER BY status, codigo_falha;

-- 8) Conferência do estoque: soma das movimentações deve bater com o estoque atual
--    (produtos criados do zero e associados a estoque 0 ou saldo prévio)
SELECT p.id_produto_servico, p.estoque AS estoque_atual,
       COALESCE(SUM(m.quantidade), 0) AS soma_movimentos
FROM servico_produto p
LEFT JOIN movimentacao_estoque m ON m.produto_id = p.id_produto_servico
WHERE p.id_produto_servico IN (SELECT produto_id FROM movimentacao_estoque)
GROUP BY p.id_produto_servico, p.estoque
ORDER BY p.id_produto_servico;
