ALTER TABLE lembrete
    ADD COLUMN data_ultimo_atendimento DATE NULL,
    ADD COLUMN data_retorno_previsto DATE NULL,
    ADD COLUMN procedimento VARCHAR(300) NULL;
