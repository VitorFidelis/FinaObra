CREATE TABLE IF NOT EXISTS despesas (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    descricao VARCHAR(1000) NOT NULL,
    categoria_id INTEGER REFERENCES categorias(id_categoria),
    fornecedor VARCHAR(255) NOT NULL,
    valor NUMERIC(10,2) NOT NULL,
    data DATE NOT NULL
);