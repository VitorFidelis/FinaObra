-- Associa categorias a um usuário específico (nullable para não quebrar dados existentes)
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS user_id UUID;

-- Associa despesas a um usuário específico (nullable para não quebrar dados existentes)
ALTER TABLE despesas ADD COLUMN IF NOT EXISTS user_id UUID;

-- Índices para acelerar consultas por usuário
CREATE INDEX IF NOT EXISTS idx_categorias_user_id ON categorias(user_id);
CREATE INDEX IF NOT EXISTS idx_despesas_user_id    ON despesas(user_id);
