CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    
    CONSTRAINT chk_username_format CHECK (
        user_name = LOWER(TRIM(user_name)) AND user_name ~ '^[a-z0-9_\.-]+$'
    )
);