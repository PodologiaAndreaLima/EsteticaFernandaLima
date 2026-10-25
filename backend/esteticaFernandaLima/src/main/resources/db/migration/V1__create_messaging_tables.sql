CREATE TABLE IF NOT EXISTS lembrete (
    id BIGINT NOT NULL AUTO_INCREMENT,
    clinica_id VARCHAR(100) NOT NULL,
    cliente_id INT NOT NULL,
    agendamento_id BIGINT NULL,
    data_hora DATETIME NOT NULL,
    data_ultimo_atendimento DATE NULL,
    data_retorno_previsto DATE NULL,
    procedimento VARCHAR(300) NULL,
    tipo VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL,
    observacao VARCHAR(500) NULL,
    criado_em DATETIME NOT NULL,
    CONSTRAINT pk_lembrete PRIMARY KEY (id),
    CONSTRAINT fk_lembrete_cliente FOREIGN KEY (cliente_id) REFERENCES cliente (id),
    CONSTRAINT uk_lembrete_clinica_cliente_horario_tipo
        UNIQUE (clinica_id, cliente_id, data_hora, tipo)
);

CREATE TABLE IF NOT EXISTS mensagem (
    id BIGINT NOT NULL AUTO_INCREMENT,
    clinica_id VARCHAR(100) NOT NULL,
    lembrete_id BIGINT NOT NULL,
    canal VARCHAR(20) NOT NULL,
    conteudo VARCHAR(2000) NOT NULL,
    telefone_destino VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    erro_provedor VARCHAR(500) NULL,
    criado_em DATETIME NOT NULL,
    enviado_em DATETIME NULL,
    CONSTRAINT pk_mensagem PRIMARY KEY (id),
    CONSTRAINT fk_mensagem_lembrete FOREIGN KEY (lembrete_id) REFERENCES lembrete (id)
);
