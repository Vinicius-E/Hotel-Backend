-- Hotel Backend Database Schema
-- Aplicando as três formas normais (1FN, 2FN, 3FN)

-- Tabela de hóspedes (1FN, 2FN, 3FN)
CREATE TABLE IF NOT EXISTS hospedes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    documento VARCHAR(11) UNIQUE NOT NULL,
    telefone VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de check-ins (1FN, 2FN, 3FN)
CREATE TABLE IF NOT EXISTS checkins (
    id BIGSERIAL PRIMARY KEY,
    hospede_id BIGINT NOT NULL,
    data_entrada TIMESTAMP NOT NULL,
    data_saida TIMESTAMP,
    adicional_veiculo BOOLEAN DEFAULT FALSE,
    valor_total DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_checkin_hospede FOREIGN KEY (hospede_id) REFERENCES hospedes(id) ON DELETE CASCADE
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_hospedes_documento ON hospedes(documento);
CREATE INDEX IF NOT EXISTS idx_hospedes_nome ON hospedes(nome);
CREATE INDEX IF NOT EXISTS idx_checkins_hospede_id ON checkins(hospede_id);
CREATE INDEX IF NOT EXISTS idx_checkins_data_entrada ON checkins(data_entrada);
CREATE INDEX IF NOT EXISTS idx_checkins_data_saida ON checkins(data_saida);
CREATE INDEX IF NOT EXISTS idx_checkins_ativo ON checkins(hospede_id, data_saida) WHERE data_saida IS NULL;

