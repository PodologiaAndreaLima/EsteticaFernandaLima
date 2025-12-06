create database EsteticaFernandaLima;
use EsteticaFernandaLima;
show tables;
    
  CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255),
    cpf VARCHAR(14),
    telefone VARCHAR(20),
    bio TEXT,
    email VARCHAR(255) UNIQUE,
    senha VARCHAR(255),
    role VARCHAR(50) NOT NULL
);

CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    data_nascimento DATE,
    CONSTRAINT cliente_cpf_unique UNIQUE (cpf),
    CONSTRAINT cliente_email_unique UNIQUE (email)
);

CREATE TABLE ordem_servico (
    id_ordem_servico INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    dt_hora DATETIME,
    observacao TEXT,
    valor_final DECIMAL(10, 2),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE custo_extra (
    id_custo_extra INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    valor DECIMAL(10, 2) NOT NULL,
    data DATE NOT NULL
);

CREATE TABLE custo_fixo (
    id_custo_fixo INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    valor_mensal DECIMAL(10, 2) NOT NULL
);

INSERT INTO usuario (nome_completo, email, senha, role)
VALUES ('Fernanda Lima', 'fernanda@lima.com', '$2a$10$8CIK425IhU.E163FXCoFD.eaGCBNvU5JGzcy.rLHkbvZv3A.Gdzsm', 'ADMIN');

select * from cliente;
select * from usuario;
select * from ordem_servico;
select * from custo_fixo;
select * from custo_extra;

INSERT INTO cliente (nome_completo, cpf, telefone, email, data_nascimento) VALUES 
('Mariana Souza', '123.456.789-01', '(11) 98888-1111', 'mariana.souza@example.com', '1995-04-12'),
('Carlos Pereira', '987.654.321-00', '(21) 97777-2222', 'carlos.pereira@example.com', '1988-10-30'),
('Fernanda Lima', '555.666.777-88', '(31) 96666-3333', 'fernanda.lima@example.com', '1999-02-18');
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(1, '2025-12-10 14:30:00', 'Limpeza de pele completa', 150.00),
(2, '2025-12-11 09:00:00', 'Massagem relaxante', 200.00),
(3, '2025-12-12 16:45:00', 'Design de sobrancelhas', 80.00);
INSERT INTO custo_extra (nome, descricao, valor, data) VALUES
('Creme Hidratante', 'Uso durante o procedimento', 25.90, '2025-12-10'),
('Luvas Descartáveis', 'Material de proteção', 12.50, '2025-12-11'),
('Máscara Facial Premium', 'Máscara utilizada em tratamento especial', 39.99, '2025-12-12');
INSERT INTO custo_fixo (nome, descricao, valor_mensal) VALUES
('Aluguel', 'Pagamento mensal do espaço comercial', 1000.00),
('Internet', 'Plano de internet fibra óptica empresarial', 100.00),
('Energia Elétrica', 'Conta mensal de luz da clínica', 200.75);

-- =========================
-- JANEIRO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(1, '2025-01-05 10:00:00', 'Limpeza de pele', 150.00),
(2, '2025-01-10 14:30:00', 'Massagem relaxante', 200.00),
(3, '2025-01-15 16:00:00', 'Design de sobrancelha', 60.00),
(1, '2025-01-20 11:45:00', 'Tratamento capilar', 180.00),
(2, '2025-01-27 09:30:00', 'Depilação', 95.00);

-- =========================
-- FEVEREIRO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(2, '2025-02-03 11:00:00', 'Limpeza facial', 140.00),
(1, '2025-02-08 15:30:00', 'Massagem terapêutica', 210.00),
(3, '2025-02-12 17:10:00', 'Sobrancelha com henna', 70.00),
(2, '2025-02-20 10:15:00', 'Peeling químico', 250.00),
(1, '2025-02-25 13:00:00', 'Depilação completa', 130.00);

-- =========================
-- MARÇO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(3, '2025-03-04 09:20:00', 'Limpeza profunda', 160.00),
(1, '2025-03-09 14:00:00', 'Massagem relaxante', 190.00),
(2, '2025-03-14 16:30:00', 'Design de sobrancelhas', 55.00),
(3, '2025-03-18 12:45:00', 'Hidratação facial', 120.00),
(1, '2025-03-27 15:10:00', 'Depilação cera quente', 110.00);

-- =========================
-- ABRIL 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(1, '2025-04-02 10:30:00', 'Limpeza de pele', 150.00),
(2, '2025-04-08 11:45:00', 'Massagem drenagem linfática', 220.00),
(3, '2025-04-15 15:20:00', 'Sobrancelhas + henna', 75.00),
(1, '2025-04-22 16:30:00', 'Peeling', 240.00),
(2, '2025-04-29 09:00:00', 'Depilação', 100.00);

-- =========================
-- MAIO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(2, '2025-05-04 09:00:00', 'Limpeza facial', 145.00),
(3, '2025-05-10 13:00:00', 'Massagem relaxante', 205.00),
(1, '2025-05-14 17:30:00', 'Design de sobrancelha', 65.00),
(2, '2025-05-23 15:10:00', 'Hidratação facial', 130.00),
(1, '2025-05-28 11:40:00', 'Depilação completa', 135.00);

-- =========================
-- JUNHO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(3, '2025-06-03 10:40:00', 'Limpeza profunda', 155.00),
(1, '2025-06-09 14:20:00', 'Massagem terapêutica', 230.00),
(2, '2025-06-14 16:00:00', 'Design de sobrancelhas', 60.00),
(1, '2025-06-22 09:50:00', 'Peeling', 245.00),
(3, '2025-06-28 12:30:00', 'Depilação', 105.00);

-- =========================
-- JULHO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(2, '2025-07-04 09:10:00', 'Limpeza facial', 150.00),
(1, '2025-07-08 10:40:00', 'Massagem relaxante', 215.00),
(3, '2025-07-12 14:30:00', 'Henna', 70.00),
(2, '2025-07-20 15:45:00', 'Peeling', 250.00),
(1, '2025-07-29 16:00:00', 'Depilação cera', 120.00);

-- =========================
-- AGOSTO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(3, '2025-08-03 11:00:00', 'Limpeza profunda', 165.00),
(1, '2025-08-08 13:10:00', 'Massagem antiestresse', 225.00),
(2, '2025-08-12 09:20:00', 'Design sobrancelha', 65.00),
(3, '2025-08-22 14:45:00', 'Hidratação facial', 135.00),
(1, '2025-08-27 16:30:00', 'Depilação', 115.00);

-- =========================
-- SETEMBRO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(2, '2025-09-02 10:20:00', 'Limpeza de pele', 155.00),
(1, '2025-09-07 11:50:00', 'Massagem relaxante', 205.00),
(3, '2025-09-13 15:00:00', 'Sobrancelha + henna', 72.00),
(2, '2025-09-21 17:30:00', 'Peeling químico', 255.00),
(1, '2025-09-29 09:15:00', 'Depilação completa', 140.00);

-- =========================
-- OUTUBRO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(1, '2025-10-03 11:10:00', 'Limpeza facial', 148.00),
(2, '2025-10-09 14:40:00', 'Massagem terapêutica', 230.00),
(3, '2025-10-15 16:20:00', 'Henna', 69.00),
(1, '2025-10-21 10:00:00', 'Peeling suave', 235.00),
(3, '2025-10-27 15:35:00', 'Depilação', 118.00);

-- =========================
-- NOVEMBRO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(2, '2025-11-05 09:40:00', 'Limpeza profunda', 160.00),
(1, '2025-11-10 12:30:00', 'Massagem relaxante', 220.00),
(3, '2025-11-16 14:10:00', 'Design de sobrancelha', 58.00),
(2, '2025-11-22 16:00:00', 'Hidratação facial', 140.00),
(1, '2025-11-29 11:50:00', 'Depilação', 125.00);

-- =========================
-- DEZEMBRO 2025
-- =========================
INSERT INTO ordem_servico (cliente_id, dt_hora, observacao, valor_final) VALUES
(1, '2025-12-04 10:10:00', 'Limpeza de pele', 155.00),
(3, '2025-12-09 15:00:00', 'Massagem premium', 250.00),
(2, '2025-12-14 16:45:00', 'Henna', 70.00),
(1, '2025-12-21 13:30:00', 'Peeling', 245.00),
(2, '2025-12-28 17:20:00', 'Depilação', 120.00);

