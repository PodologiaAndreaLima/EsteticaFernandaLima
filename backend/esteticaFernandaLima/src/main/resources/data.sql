INSERT INTO usuario (nome_completo, email, senha, role)
VALUES ('Fernanda Lima', 'fernanda@lima.com', '$2a$10$8CIK425IhU.E163FXCoFD.eaGCBNvU5JGzcy.rLHkbvZv3A.Gdzsm', 'ADMIN');

INSERT INTO usuario (nome_completo, email, senha, role)
VALUES ('João Silva', 'joao@lima.com', '$2a$10$8CIK425IhU.E163FXCoFD.eaGCBNvU5JGzcy.rLHkbvZv3A.Gdzsm', 'USER');

-- Inserts para a classe Cliente
INSERT INTO cliente (nome_completo, cpf, telefone, email, data_nascimento)
VALUES ('Ana Souza', '12345678901', '(11) 98765-4321', 'ana@souza.com', '1990-05-15');

INSERT INTO cliente (nome_completo, cpf, telefone, email, data_nascimento)
VALUES ('Carlos Pereira', '98765432100', '(21) 99876-5432', 'carlos@pereira.com', '1985-10-20');

INSERT INTO cliente (nome_completo, cpf, telefone, email, data_nascimento)
VALUES ('Mariana Oliveira', '45678912300', '(31) 91234-5678', 'mariana@oliveira.com', '1995-03-10');

-- Inserts para a classe OrdemServico
INSERT INTO ordem_servico (valor_final, dt_hora, observacao, cliente_id)
VALUES (150.00, '2025-10-01', 'Corte e hidratação', 1);

INSERT INTO ordem_servico (valor_final, dt_hora, observacao, cliente_id)
VALUES (300.00, '2025-12-05', 'Coloração e escova', 2);

INSERT INTO ordem_servico (valor_final, dt_hora, observacao, cliente_id)
VALUES (200.00, '2025-12-10', 'Manicure e pedicure', 3);

INSERT INTO ordem_servico (valor_final, dt_hora, observacao, cliente_id)
VALUES (400.00, '2025-12-11', 'Manicure e pedicure', 2);

INSERT INTO ordem_servico (valor_final, dt_hora, observacao, cliente_id)
VALUES (300.00, '2025-12-12', 'Manicure e pedicure', 1);

-- Inserts para a classe CustoExtra
INSERT INTO custo_extra (nome, descricao, valor, data)
VALUES ('Compra de produtos', 'Shampoos e condicionadores', 100.00, '2025-12-15');

INSERT INTO custo_extra (nome, descricao, valor, data)
VALUES ('Reparo de equipamentos', 'Conserto do secador', 50.00, '2025-12-20');

INSERT INTO custo_extra (nome, descricao, valor, data)
VALUES ('Treinamento', 'Curso de especialização', 10.00, '2025-12-25');

-- Inserts para a classe CustoFixo
INSERT INTO custo_fixo (nome, descricao, valor_mensal)
VALUES ('Aluguel', 'Aluguel do espaço', 200.00);

INSERT INTO custo_fixo (nome, descricao, valor_mensal)
VALUES ('Internet', 'Plano de internet', 100.00);

INSERT INTO custo_fixo (nome, descricao, valor_mensal)
VALUES ('Energia elétrica', 'Conta de luz', 150.00);