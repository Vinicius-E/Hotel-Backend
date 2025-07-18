-- Dados de exemplo para testes
-- Inserindo hóspedes de exemplo

INSERT INTO hospedes (nome, documento, telefone) VALUES 
('João Silva', '12345678901', '11999887766'),
('Maria Santos', '98765432109', '11888776655'),
('Pedro Oliveira', '45678912345', '11777665544'),
('Ana Costa', '78912345678', '11666554433'),
('Carlos Ferreira', '32165498712', '11555443322')
ON CONFLICT (documento) DO NOTHING;

-- Inserindo alguns check-ins de exemplo
INSERT INTO checkins (hospede_id, data_entrada, data_saida, adicional_veiculo, valor_total) VALUES 
(1, '2024-07-10 14:00:00', '2024-07-12 10:30:00', false, 240.00),
(2, '2024-07-11 15:30:00', '2024-07-13 11:00:00', true, 270.00),
(3, '2024-07-12 16:00:00', null, false, null)
ON CONFLICT DO NOTHING;

