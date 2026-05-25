CREATE TABLE IF NOT EXISTS verification_tokens (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_verification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_verification_token ON verification_tokens (token);