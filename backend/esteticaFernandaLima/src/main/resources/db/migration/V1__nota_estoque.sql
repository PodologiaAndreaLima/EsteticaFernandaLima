-- MySQL 8.0.16+ / InnoDB; run only after verifying legacy table definitions.
-- DDL commits implicitly in MySQL: never assume this file is atomic or auto-rollbackable.
CREATE TABLE nota_fiscal_importacao (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  versao BIGINT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL,
  imagem_digest CHAR(64) NOT NULL,
  imagem_chave VARCHAR(128) NULL,
  estado_limpeza VARCHAR(20) NOT NULL,
  limpeza_codigo VARCHAR(40) NULL,
  limpeza_em DATETIME(6) NULL,
  limpeza_tentativas INT NOT NULL DEFAULT 0,
  fornecedor_cnpj VARCHAR(14) NULL,
  fornecedor_nome VARCHAR(200) NULL,
  data_emissao DATE NULL,
  total_nota DECIMAL(19,2) NULL,
  chave_acesso VARCHAR(44) NULL,
  codigo_falha VARCHAR(40) NULL,
  criada_em DATETIME(6) NOT NULL,
  finalizada_em DATETIME(6) NULL,
  UNIQUE KEY uq_nota_dono (id, usuario_id),
  KEY ix_nota_limpeza (estado_limpeza, limpeza_em),
  CONSTRAINT fk_nota_usuario FOREIGN KEY (usuario_id) REFERENCES usuario.usuario(id),
  CONSTRAINT ck_nota_status CHECK (status IN ('PROCESSANDO','EM_REVISAO','CONFIRMADA','FALHA','DESCARTADA')),
  CONSTRAINT ck_nota_limpeza CHECK (estado_limpeza IN ('DISPONIVEL','PENDENTE','FALHOU','EXCLUIDA')),
  CONSTRAINT ck_nota_imagem CHECK (
    (estado_limpeza = 'EXCLUIDA' AND imagem_chave IS NULL)
    OR (estado_limpeza <> 'EXCLUIDA' AND imagem_chave IS NOT NULL)),
  CONSTRAINT ck_nota_final CHECK (status IN ('PROCESSANDO','EM_REVISAO') OR estado_limpeza <> 'DISPONIVEL'),
  CONSTRAINT ck_nota_tentativas CHECK (limpeza_tentativas >= 0),
  CONSTRAINT ck_nota_total CHECK (total_nota IS NULL OR total_nota >= 0)
) ENGINE=InnoDB;

CREATE TABLE nota_fiscal_item (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nota_id BIGINT NOT NULL,
  ordem INT NOT NULL,
  texto_original VARCHAR(500) NOT NULL,
  quantidade INT NULL,
  valor_unitario DECIMAL(19,2) NULL,
  valor_total DECIMAL(19,2) NULL,
  produto_sugerido_id INT NULL,
  confianca VARCHAR(10) NULL,
  status VARCHAR(20) NOT NULL,
  motivo_rejeicao VARCHAR(40) NULL,
  rejeicao_texto VARCHAR(160) NULL,
  UNIQUE KEY uq_nota_item_ordem (nota_id, ordem),
  CONSTRAINT fk_item_nota FOREIGN KEY (nota_id) REFERENCES nota_fiscal_importacao(id),
  CONSTRAINT fk_item_sugestao FOREIGN KEY (produto_sugerido_id) REFERENCES servico_produto.servico_produto(id_produto_servico),
  CONSTRAINT ck_item_ordem CHECK (ordem >= 0),
  CONSTRAINT ck_item_quantidade CHECK (quantidade IS NULL OR quantidade > 0),
  CONSTRAINT ck_item_unitario CHECK (valor_unitario IS NULL OR valor_unitario >= 0),
  CONSTRAINT ck_item_total CHECK (valor_total IS NULL OR valor_total >= 0),
  CONSTRAINT ck_item_status CHECK (status IN ('PENDENTE','VALIDO','INVALIDO','CONFIRMADO','REJEITADO')),
  CONSTRAINT ck_item_confianca CHECK (confianca IS NULL OR confianca IN ('ALTA','MEDIA','BAIXA'))
) ENGINE=InnoDB;

CREATE TABLE movimentacao_estoque (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  item_id BIGINT NOT NULL,
  produto_id INT NOT NULL,
  quantidade INT NOT NULL,
  custo_unitario DECIMAL(19,2) NOT NULL,
  estoque_apos INT NOT NULL,
  valor_venda_apos DECIMAL(19,2) NULL,
  registrada_em DATETIME(6) NOT NULL,
  UNIQUE KEY uq_movimento_item (item_id),
  KEY ix_movimento_produto (produto_id),
  CONSTRAINT fk_movimento_item FOREIGN KEY (item_id) REFERENCES nota_fiscal_item(id),
  CONSTRAINT fk_movimento_produto FOREIGN KEY (produto_id) REFERENCES servico_produto.servico_produto(id_produto_servico),
  CONSTRAINT ck_movimento_quantidade CHECK (quantidade > 0),
  CONSTRAINT ck_movimento_custo CHECK (custo_unitario BETWEEN 0.01 AND 9999999.99),
  CONSTRAINT ck_movimento_estoque CHECK (estoque_apos >= 0)
) ENGINE=InnoDB;

CREATE TABLE nota_fiscal_job (
  nota_id BIGINT NOT NULL PRIMARY KEY,
  versao BIGINT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL,
  tentativas INT NOT NULL DEFAULT 0,
  lease_ate DATETIME(6) NULL,
  criado_em DATETIME(6) NOT NULL,
  KEY ix_job_lease (status, lease_ate),
  CONSTRAINT fk_job_nota FOREIGN KEY (nota_id) REFERENCES nota_fiscal_importacao(id),
  CONSTRAINT ck_job_status CHECK (status IN ('PENDENTE','EM_EXECUCAO','CONCLUIDO','CANCELADO')),
  CONSTRAINT ck_job_tentativas CHECK (tentativas BETWEEN 0 AND 3)
) ENGINE=InnoDB;

CREATE TABLE nota_fiscal_identidade (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  usuario_id BIGINT NOT NULL,
  nota_id BIGINT NOT NULL,
  tipo_identidade VARCHAR(24) NOT NULL,
  identidade_digest CHAR(64) NOT NULL,
  UNIQUE KEY uq_nota_identidade_dono (usuario_id, tipo_identidade, identidade_digest),
  KEY ix_identidade_nota_dono (nota_id, usuario_id),
  CONSTRAINT fk_identidade_dono FOREIGN KEY (nota_id, usuario_id)
    REFERENCES nota_fiscal_importacao(id, usuario_id),
  CONSTRAINT ck_identidade_tipo CHECK (tipo_identidade IN ('CHAVE_ACESSO','DADOS_E_IMAGEM','SOMENTE_IMAGEM'))
) ENGINE=InnoDB;
