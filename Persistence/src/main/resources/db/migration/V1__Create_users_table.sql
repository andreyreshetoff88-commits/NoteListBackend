CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    is_verified BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_users_phone_number ON users (phone_number);