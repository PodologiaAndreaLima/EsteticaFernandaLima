-- =============================
-- DADOS INICIAIS
-- =============================

-- Usuário padrão (id = 1)
INSERT INTO usuario (nome_completo, email, senha, role)
VALUES ('Fernanda Lima', 'fernanda@lima.com', '$2a$10$8CIK425IhU.E163FXCoFD.eaGCBNvU5JGzcy.rLHkbvZv3A.Gdzsm', 'ADMIN');

-- Clientes
INSERT INTO cliente (nome_completo, cpf, telefone, email, data_nascimento) VALUES
('Mariana Souza', '123.456.789-01', '(11) 98888-1111', 'mariana.souza@example.com', '1995-04-12'),
('Carlos Pereira', '987.654.321-00', '(21) 97777-2222', 'carlos.pereira@example.com', '1988-10-30'),
('Fernanda Lima', '555.666.777-88', '(31) 96666-3333', 'fernanda.lima@example.com', '1999-02-18');

