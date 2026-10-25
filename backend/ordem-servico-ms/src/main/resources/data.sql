-- Exemplos locais: clientes 1, 2 e 3 e usuario 1 do backend principal.
-- IDs 1001 a 1005 reservados para esta carga; registros existentes nao sao alterados.
INSERT INTO ordem_servico_ms (id, cliente_id, usuario_id, valor_final, data_criacao, dt_hora, status, retorno, observacao)
SELECT 1001, 1, 1, 150.00, '2026-10-25', '2026-10-26 09:00:00', 'PENDENTE', FALSE, 'Exemplo: atendimento aguardando inicio.'
WHERE NOT EXISTS (SELECT 1 FROM ordem_servico_ms WHERE id = 1001);

INSERT INTO ordem_servico_ms (id, cliente_id, usuario_id, valor_final, data_criacao, dt_hora, status, retorno, observacao)
SELECT 1002, 2, 1, 200.00, '2026-10-25', '2026-10-25 14:30:00', 'EM_ANDAMENTO', FALSE, 'Exemplo: atendimento em andamento.'
WHERE NOT EXISTS (SELECT 1 FROM ordem_servico_ms WHERE id = 1002);

INSERT INTO ordem_servico_ms (id, cliente_id, usuario_id, valor_final, data_criacao, dt_hora, status, retorno, observacao)
SELECT 1003, 3, 1, 80.00, '2026-10-24', '2026-10-24 16:00:00', 'CONCLUIDA', FALSE, 'Exemplo: atendimento concluido.'
WHERE NOT EXISTS (SELECT 1 FROM ordem_servico_ms WHERE id = 1003);

INSERT INTO ordem_servico_ms (id, cliente_id, usuario_id, valor_final, data_criacao, dt_hora, status, retorno, observacao)
SELECT 1004, 1, 1, 0.00, '2026-10-24', '2026-10-25 10:00:00', 'CANCELADA', FALSE, 'Exemplo: atendimento cancelado.'
WHERE NOT EXISTS (SELECT 1 FROM ordem_servico_ms WHERE id = 1004);

INSERT INTO ordem_servico_ms (id, cliente_id, usuario_id, valor_final, data_criacao, dt_hora, status, retorno, observacao)
SELECT 1005, 2, 1, 0.00, '2026-10-25', '2026-11-05 00:00:00', 'PENDENTE', TRUE, 'Exemplo: retorno aguardando definicao do horario.'
WHERE NOT EXISTS (SELECT 1 FROM ordem_servico_ms WHERE id = 1005);
